/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.xmlexport;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Container;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.StartupEnvironment;
import org.rapla.plugin.RaplaExtensionPoints;
import org.rapla.servletpages.DefaultHTMLMenuEntry;
import org.rapla.servletpages.HTMLMenuExtensionPoint;
///import org.rapla.servletpages.RaplaResourcePageGenerator;


/**
   This is a demonstration of a rapla-plugin. It adds a sample usecase and option
   to the rapla-system.
 */

public class XMLExportPlugin
    implements
    PluginDescriptor
{
    public static final String PLUGIN_CLASS = XMLExportPlugin.class.getName();

    public String toString() {
        return "XML Export";
    }
    
    /**
     * @see org.rapla.framework.PluginDescriptor#provideServices(org.rapla.framework.general.Container)
     */
    public void provideServices(Container container, Configuration config)  {
        if ( !config.getAttributeAsBoolean("enabled", false) )
        	return;

        StartupEnvironment env = container.getStartupEnvironment();
        if ( env.getStartupMode() == StartupEnvironment.SERVLET) {
        	
            container.addContainerProvidedComponent( RaplaExtensionPoints.SERVLET_PAGE_EXTENSION, XMLPageGenerator.class.getName(),"xmlexport", config);
            
            try {
                    HTMLMenuExtensionPoint mainMenu = (HTMLMenuExtensionPoint)container.getContext().lookup( RaplaExtensionPoints.HTML_MAIN_MENU_EXTENSION_POINT );
                    mainMenu.insert( new DefaultHTMLMenuEntry("XML-EXPORT","rapla?page=xmlexport"));

            } catch ( Exception ex) {
                //getLogger().error("Could not initialize xmlexport plugin on server" , ex);
            }
        }        
    }

    public Object getPluginMetaInfos( String key )
    {
        return null;
    }

}

