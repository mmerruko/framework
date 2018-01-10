package com.vaadin.osgi.servlet.ds;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class OsgiUIProvider extends DefaultUIProvider {
	private Map<Class<? extends UI>, ServiceObjects<UI>> registeredUIs = Collections
			.synchronizedMap(new LinkedHashMap<>());
	private Map<String, Class<? extends UI>> classNameToClassMap = Collections.synchronizedMap(new LinkedHashMap<>());
	private Optional<LogService> logService = Optional.empty();

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        VaadinRequest request = event.getRequest();

        String uiClassName = request.getService().getDeploymentConfiguration()
                .getUIClassName();
        if (uiClassName != null) {
        	return classNameToClassMap.get(uiClassName);
        }

        return null;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		ServiceObjects<UI> serviceObjects = registeredUIs.get(event.getUIClass());
		if (serviceObjects != null) {

			ServiceReference<UI> reference = serviceObjects.getServiceReference();
	        Object property = reference.getProperty(Constants.SERVICE_SCOPE);
	        if (Constants.SCOPE_PROTOTYPE.equals(property)) {
	        	UI service = serviceObjects.getService();
				service.addDetachListener(e -> serviceObjects.ungetService(service));
				return service;
	        } else {
				logService.ifPresent(log -> log.log(LogService.LOG_WARNING,
						"UI services should have a prototype scope! Creating UI instance using the default constructor!"));
	        	return super.createInstance(event);
	        }
		}
		return null;
	}

	public void bindUI(UI ui, ServiceObjects<UI> objects) {
		synchronized (this) {
			classNameToClassMap.put(ui.getClass().getName(), ui.getClass());
			registeredUIs.put(ui.getClass(), objects);
		}
	}

	public void unbindUI(UI ui) {
		synchronized (this) {
			classNameToClassMap.remove(ui.getClass().getName());
			registeredUIs.remove(ui.getClass());
		}
	}

	public void setLogService(LogService logService) {
		this.logService = Optional.ofNullable(logService);
	}
}
