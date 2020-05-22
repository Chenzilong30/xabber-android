/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 *
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 *
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package com.xabber.android.data.roster;

import android.graphics.drawable.Drawable;

import com.xabber.android.data.account.StatusMode;
import com.xabber.android.data.entity.AccountJid;
import com.xabber.android.data.entity.BaseEntity;
import com.xabber.android.data.entity.ContactJid;
import com.xabber.android.data.extension.avatar.AvatarManager;
import com.xabber.android.data.extension.capability.CapabilitiesManager;
import com.xabber.android.data.extension.capability.ClientInfo;
import com.xabber.android.data.extension.capability.ClientSoftware;
import com.xabber.android.data.extension.vcard.VCardManager;

import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.Collections;


/**
 * Basic contact representation.
 *
 * @author alexander.ivanov
 */
public class AbstractContact extends BaseEntity {

    private long lastTimeSearched = 0;

    protected AbstractContact(AccountJid account, ContactJid user) {
        super(account, user);
    }

    /**
     * vCard and roster can be used for name resolving.
     *
     * @return Verbose name.
     */
    public String getName() {
        String vCardName = VCardManager.getInstance().getName(user.getJid());

        if (!"".equals(vCardName))
            return vCardName;

        return user.toString();
    }

    public StatusMode getStatusMode() {
        return StatusMode.createStatusMode(RosterManager.getInstance().getPresence(account, user));
    }

    public boolean isSubscribed() {
        return RosterManager.getInstance().accountIsSubscribedTo(account, user);
    }

    public String getStatusText() {
        final String statusText = PresenceManager.getInstance().getStatusText(account, user);
        if (statusText == null) {
            return "";
        } else {
            return statusText;
        }
    }

    public ClientSoftware getClientSoftware() {
        final Presence presence = RosterManager.getInstance().getPresence(account, user);

        if (presence == null || !presence.isAvailable()) {
            return ClientSoftware.unknown;
        }

        ClientInfo clientInfo = CapabilitiesManager.getInstance().getCachedClientInfo(presence.getFrom());
        if (clientInfo == null) {
            return ClientSoftware.unknown;
        } else {
            return clientInfo.getClientSoftware();
        }
    }

    public Collection<? extends Circle> getGroups() {
        return Collections.emptyList();
    }

    public Drawable getAvatar() {
        return getAvatar(true);
    }

    public Drawable getAvatar(boolean isDefaultAvatarAccepted) {
        if (isDefaultAvatarAccepted)
            return AvatarManager.getInstance().getUserAvatarForContactList(user, getName());
        else
            return AvatarManager.getInstance().getUserAvatarForVcard(user);
    }

    /**
     * @return Whether contact is connected.
     */
    public boolean isConnected() {
        return true;
    }

}
