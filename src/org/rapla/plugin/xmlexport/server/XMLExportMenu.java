package org.rapla.plugin.xmlexport.server;

import org.rapla.framework.RaplaContext;
import org.rapla.servletpages.DefaultHTMLMenuEntry;

public class XMLExportMenu extends DefaultHTMLMenuEntry {

	public XMLExportMenu(RaplaContext context) {
		super(context);
	}

	public String getLinkName() {
		return "rapla?page=xmlexport";
	}

	public String getName() {
    	return "XML-EXPORT";
    }
}
