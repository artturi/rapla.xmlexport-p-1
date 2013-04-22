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
import org.rapla.client.ClientServiceContainer;
import org.rapla.framework.Configuration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;


public class XMLExportPlugin implements  PluginDescriptor<ClientServiceContainer>
{

    public static final String PLUGIN_CLASS = XMLExportPlugin.class.getName();

    public void provideServices(ClientServiceContainer container, Configuration config) throws RaplaContextException  {

    }

}

