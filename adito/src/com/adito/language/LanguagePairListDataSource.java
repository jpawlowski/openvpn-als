package com.adito.language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.adito.properties.Pair;
import com.adito.properties.PairListDataSource;

/**
 * Implementation of {@link PairListDataSource} that retrieves its data from
 * the list of installed language packs. This is currently used to select the
 * default language. 
 */
public class LanguagePairListDataSource implements PairListDataSource {

    /* (non-Javadoc)
     * @see com.adito.properties.PairListDataSource#getValues(javax.servlet.http.HttpServletRequest)
     */
    public List getValues(HttpServletRequest request) {
        List<Pair> l = new ArrayList<Pair>();
        for(Iterator i = LanguagePackManager.getInstance().languages(true); i.hasNext(); ) {
            Language lang  = (Language)i.next();
            l.add(new Pair(lang.getCode(), lang.getDescription()));
        }
        return l;
    }

}
