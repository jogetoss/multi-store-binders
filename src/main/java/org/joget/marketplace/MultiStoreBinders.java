package org.joget.marketplace;

import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormBinder;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormStoreBinder;
import org.joget.apps.form.model.FormStoreElementBinder;
import org.joget.apps.form.model.FormStoreMultiRowElementBinder;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.workflow.model.WorkflowAssignment;

public class MultiStoreBinders extends FormBinder implements FormStoreBinder, FormStoreElementBinder, FormStoreMultiRowElementBinder {
    
    private final static String MESSAGE_PATH = "messages/multiStoreBinders";
    
    public String getName() {
        return "Multi Store Binders";
    }

    public String getVersion() {
        return "7.0.1";
    }
    
    public String getClassName() {
        return getClass().getName();
    }

    public String getLabel() {
        //support i18n
        return AppPluginUtil.getMessage("org.joget.marketplace.MultiStoreBinders.pluginLabel", getClassName(), MESSAGE_PATH);
    }
    
    public String getDescription() {
        //support i18n
        return AppPluginUtil.getMessage("org.joget.marketplace.MultiStoreBinders.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/multiStoreBinders.json", null, true, MESSAGE_PATH);
    }

    public FormRowSet store(final Element element, final FormRowSet rows, final FormData formData) {
        Object[] binders = (Object[]) getProperty("binders");
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        if (binders != null && binders.length > 0) {                  
            for (Object binder : binders) {
                if (binder != null && binder instanceof Map) {
                    Map binderMap = (Map) binder;
                    if (binderMap.containsKey("className") && !binderMap.get("className").toString().isEmpty()) {
                        String className = binderMap.get("className").toString();
                        FormStoreBinder p = (FormStoreBinder) pluginManager.getPlugin(className);
                        if (p != null) {
                            Map propertiesMap = new HashMap();
                            propertiesMap.putAll(AppPluginUtil.getDefaultProperties((Plugin) p, (Map) binderMap.get("properties"), (AppDefinition) AppUtil.getCurrentAppDefinition(), null));

                            if (p instanceof PropertyEditable) {
                                ((PropertyEditable) p).setProperties(propertiesMap);
                            }
                            p.store(element, rows, formData);
                        }
                    }
                }
            }        
        }
        return null;
    }
}
