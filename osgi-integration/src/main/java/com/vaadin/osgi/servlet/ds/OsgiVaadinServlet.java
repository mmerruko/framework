package com.vaadin.osgi.servlet.ds;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class OsgiVaadinServlet extends VaadinServlet {
	private static final long serialVersionUID = 1L;

	private OsgiUIProvider osgiUIProvider;

	@Override
	protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {

		OsgiVaadinServletService osgiVaadinServletService = new OsgiVaadinServletService(deploymentConfiguration, this, osgiUIProvider);
		osgiVaadinServletService.init();

		return osgiVaadinServletService;
	}

	public void setUIProvider(OsgiUIProvider osgiUIProvider) {
		this.osgiUIProvider = osgiUIProvider;
	}

}
