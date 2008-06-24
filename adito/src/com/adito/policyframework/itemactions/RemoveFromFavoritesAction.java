/**
 * 
 */
package com.adito.policyframework.itemactions;

import com.adito.navigation.AbstractFavoriteItem;
import com.adito.security.SessionInfo;
import com.adito.table.AvailableTableItemAction;

public final class RemoveFromFavoritesAction extends AbstractPathAction {

    public static final String TABLE_ITEM_ACTION_ID = "removeFromFavorites";

    public RemoveFromFavoritesAction(String messageResourcesKey) {
        super(TABLE_ITEM_ACTION_ID, messageResourcesKey, 100, false, SessionInfo.USER_CONSOLE_CONTEXT,
                        "{2}.do?actionTarget=removeFavorite&selectedResource={0}");
    }

    public boolean isEnabled(AvailableTableItemAction availableItem) {
        AbstractFavoriteItem item = (AbstractFavoriteItem) availableItem.getRowItem();
        return item.getFavoriteType().equals(AbstractFavoriteItem.USER_FAVORITE);
    }
}