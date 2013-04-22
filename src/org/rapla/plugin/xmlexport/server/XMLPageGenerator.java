package org.rapla.plugin.xmlexport.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rapla.components.util.IOUtil;
import org.rapla.components.util.SerializableDateTimeFormat;
import org.rapla.entities.Category;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.AppointmentBlock;
import org.rapla.entities.domain.AppointmentBlockStartComparator;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.AttributeType;
import org.rapla.entities.dynamictype.Classification;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.storage.RefEntity;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.servletpages.RaplaPageGenerator;


public class XMLPageGenerator  extends RaplaComponent implements RaplaPageGenerator
{
    public XMLPageGenerator(RaplaContext context) 
    {
        super( context );
    }
    
    public void generatePage( ServletContext context, HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException
    {
        java.io.PrintWriter out = response.getWriter();        
        try
        {                  
            /* Http Arguments :
             * http://127.0.0.1:8051/rapla?page=xmlexport&user=admin&start=2007-06-01&end=2007-06-29
             * user : user who exported, admin
             * start : start date in YYY-MM-DD format
             * end : end date in YYY-MM-DD format
             * keys : event or resource types separated by ',' (keys=defaultReservation,)
             */
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	// Pass request for treatment
            List<AppointmentBlock> blocks = getBlocksForRequest(request, out);

            response.setContentType ( "text/xml; charset=ISO-8859-15" );
            out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-15\" ?>");
            out.println("<export>");
            
            Map <?,?> parameters = request.getParameterMap();
            String outputFormat = (parameters.containsKey("format") == true)? request.getParameter("format") : "default" ;
            
            for ( int i=0;i< blocks.size();i++)
            {
            	out.println("  <block contextid=\"" + i + "\">");            	
                AppointmentBlock block = blocks.get( i );
				Appointment app = block.getAppointment();
                Reservation reservation = app.getReservation();
                
                out.println("    <start>" + sdf.format( new Date(block.getStart()) ) + "</start>");
                out.println("    <end>" + sdf.format( new Date(block.getEnd()) ) + "</end>");
                out.println("    <name>" + reservation.getName(getLocale()).replaceAll("&", "-") + "</name>");
                
                Allocatable[] persons = reservation.getPersons();
                Allocatable[] resources = reservation.getResources();
                
                if ( outputFormat.equals( new String("limited") ) == true )
                {
                	this.allocatableToLimitedXML(persons, true, out); 
                    this.allocatableToLimitedXML(resources, false, out);
                }
                else
                {
                	this.allocatableToXML(persons, true, out); 
                    this.allocatableToXML(resources, false, out);
                } 
                
                
                out.println("  </block>");
            }
            out.println("</export>");
            
        }
        catch ( Exception ex )
        {        	
            out.println( IOUtil.getStackTraceAsString ( ex ) );
            throw new ServletException( ex );
        }
        
    }
    
    private List<AppointmentBlock> getBlocksForRequest(HttpServletRequest request, java.io.PrintWriter out) throws ServletException {
        Map<?,?>  parameters = request.getParameterMap();
        SerializableDateTimeFormat format = new SerializableDateTimeFormat();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
	        Date start = new Date();
	        Date end = new Date(start.getTime() + 86400*1000);
	        if (parameters.containsKey("start") == true) {
	        	String startString = request.getParameter ( "start" );
	        	start = format.parseDate( startString, false );
	        }
	        if (parameters.containsKey("end") == true) {
	        	String endString = request.getParameter ( "end" );
	        	end = format.parseDate( endString, true);
	        }
	         
	        //String username = (parameters.containsKey("user"))? request.getParameter( "user" ) : "admin"; 
	        //User user = getQuery().getUser( username );           
	
	        // Filtering Resources
	        ArrayList<ClassificationFilter> filters = new ArrayList<ClassificationFilter>();
	        if (parameters.containsKey("keys"))
	        {
	            String[] eventTypes = request.getParameter("keys").split(",");
	            for (int i=0;i<eventTypes.length ;i++)
	            {
	                DynamicType oneType = getQuery().getDynamicType(eventTypes[i]);
	                ClassificationFilter oneFilter =  oneType.newClassificationFilter();
	                filters.add( oneFilter );
	            }
	        }
	        ClassificationFilter[] cfilters  = new ClassificationFilter[filters.size()];            
	        if ( filters.size() == 0 )
	        {
	        	cfilters = null;
	        }
	        else
	        {
	        	for (int i=0;i< filters.size ();i++)
	        		cfilters[i] =  filters.get(i);
	        }
	        Allocatable[] allocatables = getQuery().getAllocatables(cfilters);
	        
	        // get events respecting http arguments and resulting filtered resources
	        Reservation[] events = getQuery().getReservations(allocatables, start, end);
	
            // build the blocks
            List<AppointmentBlock> blocks = new ArrayList<AppointmentBlock>();
            for ( int i=0;i<events.length;i++)
            {
                Reservation event = events[i];
                Appointment[] appointments = event.getAppointments();
                for ( int j=0;j< appointments.length;j++)
                {
                    Appointment appointment = appointments[j];
                    appointment.createBlocks ( start, end, blocks );
                }
            }
            // sort them by time
            Collections.sort(blocks, new AppointmentBlockStartComparator());
            
            return blocks;
            
        }
        catch ( Exception ex )
        {
            out.println( IOUtil.getStackTraceAsString ( ex ) );
            throw new ServletException( ex );
        }
    }

    /**
     * allocatableToXML loops through an array of allocatables, fetches every attributes, and outputs in xml
     * 
     */
    public void allocatableToXML(Allocatable[] alls, boolean isPerson, java.io.PrintWriter out) {
    	String blockName = (isPerson)? "person" : "resource";
        if (alls.length > 0) {
            out.println("    <" + blockName + "s>");
            for (int j=0;j<alls.length;j++) {
            	Classification classification = alls[j].getClassification();
            	String allKey = classification.getType().getElementKey();
            	Attribute[] attributes = classification.getAttributes();
            	String allocatableId = ((RefEntity<?>) alls[j]).getId().toString();
            	if ( allocatableId.contains("_") == true) {
            		allocatableId = allocatableId.split("_")[1];
            	}
                out.println("      <" + blockName + " typekey='" + allKey + "' relid='" + allocatableId + "' >");
                out.println("        <" + blockName + "Type>" + classification.getType().getName(getLocale()) + "</" + blockName + "Type>");
                out.println("        <displayName>" + alls[j].getName(getLocale()).replace("&", "") + "</displayName>");
                for (int k=0; k<attributes.length; k++)
                {      
                	String attributeKey = attributes[k].getKey();
                	if (attributes[k].getType() == AttributeType.CATEGORY) {
                		Category cat = (Category) classification.getValue(attributeKey);  
                		if ( cat != null ) {
                			out.println("        <" + attributeKey + ">" + cat.getName(getLocale()).replace("&", "") + "</" + attributeKey + ">");
                		}
                		else {
                			out.println("        <" + attributeKey + ">null</" + attributeKey + ">");
                		}
                	}
                	else {
                		Object attributeValue = classification.getValue(attributeKey);
                		if ( attributeValue != null) {
                			out.println("        <" + attributeKey + ">" + attributeValue.toString().replace("&", "") + "</" + attributeKey + ">");
                		}
                		else {
                			out.println("        <" + attributeKey + ">null</" + attributeKey + ">");
                		}                		
                	}
                }
                out.println("      </" + blockName + ">");
            }
            out.println("    </" + blockName + "s>");
        }    	
    }
    
    /**
     * allocatableToXML loops through an array of allocatables, fetches the minimum required to build sql queries.
     * 
     */    
    public void allocatableToLimitedXML(Allocatable[] alls, boolean isPerson, java.io.PrintWriter out) {
    	String blockName = (isPerson)? "person" : "resource";
        if (alls.length > 0) {
            out.println("    <" + blockName + "s>");
            for (int j=0;j<alls.length;j++) {
            	Classification classification = alls[j].getClassification();
            	String allKey = classification.getType().getElementKey();
            	String allocatableId = ((RefEntity<?>) alls[j]).getId().toString();
            	if ( allocatableId.contains("_") == true) {
            		allocatableId = allocatableId.split("_")[1];
            	}
                out.println("      <" + blockName + " typekey='" + allKey + "' relid='" + allocatableId + "' />");
            }
            out.println("    </" + blockName + "s>");
        }    	
    }    
} 