/*
 */
package net.openvpn.als.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;

import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.boot.TypeMetaListItem;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.input.MultiSelectDataSource;
import net.openvpn.als.input.MultiSelectSelectionModel;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;

/**
 * Wrapper bean used for displaying
 * {@link net.openvpn.als.boot.PropertyDefinition}s and their values in the
 * various configuration pages.
 */
public class PropertyItemImpl extends AbstractPropertyItem {

    final static Log log = LogFactory.getLog(PropertyItemImpl.class);

    // Private instance variables

    MultiSelectSelectionModel listDataSourceModel;
    HttpServletRequest request;

    /**
     * Constructor
     * 
     * @param request the request object used to display the bean
     * @param definition the property definition to display
     * @param value the value to display
     * @throws IllegalArgumentException if value is invalid
     */
    public PropertyItemImpl(HttpServletRequest request, PropertyDefinition definition, String value) {
        super(definition, request);

        if (value == null) {
            throw new IllegalArgumentException("Value may not be null.");
        }

        this.request = request;
        this.definition = definition;

        rows = 0;
        columns = 0;

        if (definition.getType() == PropertyDefinition.TYPE_LIST) {
            List<Pair> listItemsList = new ArrayList<Pair>();
            if (!definition.getTypeMeta().startsWith("!")) {
                for (Iterator i = ((List) definition.getTypeMetaObject()).iterator(); i.hasNext();) {
                    TypeMetaListItem item = (TypeMetaListItem) i.next();
                    ServletContext context = CoreServlet.getServlet().getServletContext();
                    ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, context);
                    String mrKey = (item.getMessageResourcesKey() == null ? "properties" : item.getMessageResourcesKey())
                                    + moduleConfig.getPrefix();
                    MessageResources res = (MessageResources) context.getAttribute(mrKey);
                    String k = definition.getName() + ".value." + item.getValue();
                    String v = "";
                    if (res != null) {
                        v = res.getMessage((Locale) request.getSession().getAttribute(Globals.LOCALE_KEY), k);
                        if (v == null) {
                            v = item.getValue();
                        }
                    }
                    Pair pair = new Pair(item.getValue(), v);
                    if (item.getValue().equals(value)) {
                        this.value = pair.getValue();
                    }
                    listItemsList.add(pair);
                }
            } else {
                String className = definition.getTypeMeta().substring(1);
                try {
                    Class clazz = Class.forName(className);
                    Object obj = clazz.newInstance();
                    if (obj instanceof PairListDataSource)
                        listItemsList.addAll(((PairListDataSource) obj).getValues(request));
                    else
                        throw new Exception("Not a PairListDataSource.");
                } catch (Exception e) {
                    log.error("Failed to create list data source.", e);
                }
                this.value = value;
            }
            listItems = new Pair[listItemsList.size()];
            listItemsList.toArray(listItems);
        } else if (definition.getType() == PropertyDefinition.TYPE_MULTI_ENTRY_LIST) {
            this.value = new PropertyList(value).getAsTextFieldText();
            StringTokenizer t = new StringTokenizer(definition.getTypeMeta(), "x");
            try {
                if (t.hasMoreTokens()) {
                    columns = Integer.parseInt(t.nextToken());
                    rows = Integer.parseInt(t.nextToken());
                }
            } catch (NumberFormatException nfe) {

            }
        } else if (definition.getType() == PropertyDefinition.TYPE_TEXT_AREA) {
            this.value = value;
            StringTokenizer t = new StringTokenizer(definition.getTypeMeta(), "x");
            try {
                columns = Integer.parseInt(t.nextToken());
                rows = Integer.parseInt(t.nextToken());
            } catch (NumberFormatException nfe) {

            }
        } else if (definition.getType() == PropertyDefinition.TYPE_BOOLEAN) {
            if (definition.getTypeMetaObject() != null) {
                String trueVal = (String) (((List) definition.getTypeMetaObject()).get(0));
                this.value = value.equals(trueVal) ? Boolean.TRUE : Boolean.FALSE;
            } else {
                this.value = Boolean.valueOf(value);
            }
        } else if (definition.getType() == PropertyDefinition.TYPE_STRING) {
            try {
                columns = Integer.parseInt(definition.getTypeMeta());
            } catch (NumberFormatException nfe) {
            }
            this.value = value;
        } else if (definition.getType() == PropertyDefinition.TYPE_INTEGER) {
            try {
                columns = Integer.parseInt(definition.getTypeMeta());
            } catch (NumberFormatException nfe) {
            }
            this.value = value;
        } else if (definition.getType() == PropertyDefinition.TYPE_PASSWORD) {
            try {
                columns = Integer.parseInt(definition.getTypeMeta());
            } catch (NumberFormatException nfe) {
            }
            this.value = value;
        } else if (definition.getType() == PropertyDefinition.TYPE_TIME_IN_MS) {
            try {
                int val = Integer.parseInt(value);
                if (definition.getTypeMeta().equalsIgnoreCase("s")) {
                    this.value = String.valueOf(val / 1000);
                } else if (definition.getTypeMeta().equalsIgnoreCase("m")) {
                    this.value = String.valueOf(val / 1000 / 60);
                } else if (definition.getTypeMeta().equalsIgnoreCase("h")) {
                    this.value = String.valueOf(val / 1000 / 60 / 60);
                } else if (definition.getTypeMeta().equalsIgnoreCase("d")) {
                    this.value = String.valueOf(val / 1000 / 60 / 60 / 24);
                } else {
                    this.value = String.valueOf(val);
                }
            } catch (Exception e) {
                this.value = value;
            }
        } else if (definition.getType() == PropertyDefinition.TYPE_MULTI_SELECT_LIST) {
            PropertyList pList = new PropertyList(value);
            this.value = pList.getAsTextFieldText();
            String clazz = definition.getTypeMeta();
            SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
            try {
                listDataSourceModel = new MultiSelectSelectionModel(session, ((MultiSelectDataSource) Class.forName(clazz)
                                .newInstance()), pList);
            } catch (Throwable t) {
                log.error("Failed to list of available of values. ", t);
            }

        } else {
            this.value = value;
        }

        // 
    }

    /**
     * Get the list data source model that may be used as the source list for
     * property definitions of type
     * {@link PropertyDefinition#TYPE_MULTI_SELECT_LIST}
     * 
     * @return list data source model
     */

    public MultiSelectSelectionModel getListDataSourceModel() {
        return listDataSourceModel;
    }

    public void setValue(Object value) {
        this.value = value;
        if (getType() == PropertyDefinition.TYPE_MULTI_SELECT_LIST) {
            PropertyList l = new PropertyList();
            l.setAsTextFieldText(getValue().toString());
            String clazz = definition.getTypeMeta();
            try {
                SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
                listDataSourceModel = new MultiSelectSelectionModel(session, ((MultiSelectDataSource) Class.forName(clazz)
                                .newInstance()), l);
            } catch (Throwable t) {
                log.error("Failed to list of available of values. ", t);
            }
        }
    }
}
