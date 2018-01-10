package com.vaadin.osgi.servlet.ds;

import java.util.Collections;
import java.util.List;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

public class OsgiVaadinServletService extends VaadinServletService {

	private OsgiUIProvider uiProvider;

	public OsgiVaadinServletService(DeploymentConfiguration deploymentConfiguration, OsgiVaadinServlet servlet,
			OsgiUIProvider uiProvider) throws ServiceException {
		super(servlet, deploymentConfiguration);
		this.uiProvider = uiProvider;
	}

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) throws ServiceException {
		VaadinSession vaadinSession = new VaadinSession(this) {
			public List<com.vaadin.server.UIProvider> getUIProviders() {
				return Collections.singletonList(uiProvider);
			}
		};

		return vaadinSession;
	}

}
