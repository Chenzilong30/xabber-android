package com.xabber.android.ui.fragment.chatListFragment

import android.view.View
import com.xabber.android.R
import com.xabber.android.data.SettingsManager
import com.xabber.android.data.account.AccountManager
import com.xabber.android.data.message.NotificationState
import com.xabber.android.data.message.chat.ChatManager
import com.xabber.android.data.notification.custom_notification.CustomNotifyPrefsManager
import com.xabber.android.data.notification.custom_notification.Key
import com.xabber.android.data.roster.AbstractContact
import com.xabber.android.ui.color.ColorManager

data class ChatItemVO(val contact: AbstractContact){
    init {
        val accountColorIndicator = getAccountColorIndicator()
        val accountColorIndicatorVisibility = getAccountColorIndicatorVisibility()
        val contactAvatar = getContactAvatar()
        val contactAvatarVisibility = getContactAvatarVisibility()
        val contactOnlyStatusVisibility = getContactOnlyStatusVisibility()
        val contactStatusVisibility = getContactStatusVisibility()
        val contactStatusLevel = getContactStatusLevel()
        val contactName = getContactName()
        val groupchatIndicator = getGroupchatIndicator()
        val groupchatIndicatorVisibility = getGroupchatIndicatorVisibility()
        val notificationMuteIcon = getNotificationMuteIcon()
        val unreadCount = getUnreadCount()
        val unreadCountVisibility = getUnreadCountVisibility()

    }

    private fun getAccountColorIndicator() = ColorManager.getInstance().accountPainter
            .getAccountMainColor(contact.account)

    private fun getAccountColorIndicatorVisibility() =
            if (AccountManager.getInstance().enabledAccounts.size > 1) View.VISIBLE else View.INVISIBLE

    private fun getContactAvatar() = contact.getAvatar(true)

    private fun getContactAvatarVisibility() = if (SettingsManager.contactsShowAvatars()) View.VISIBLE
            else View.GONE

    private fun getContactOnlyStatusVisibility() = if (SettingsManager.contactsShowAvatars()) View.VISIBLE
            else View.GONE

    private fun getContactStatusVisibility() : Int {
        val statusLevel = contact.statusMode.statusLevel
        val isServer = contact.user.jid.isDomainBareJid
        val isGroupchat = ChatManager.getInstance().getOrCreateChat(contact.account, contact.user)
                .isGroupchat
        if (((statusLevel == 6 || contact.user.jid.isDomainBareJid) || statusLevel != 1)
                || !SettingsManager.contactsShowAvatars() || isServer || isGroupchat ) return View.GONE
        else  return View.VISIBLE
    }

    private fun getContactStatusLevel() = contact.statusMode.statusLevel

    private fun getContactName() = contact.name

    private fun getGroupchatIndicatorVisibility() : Int {
        val isServer = contact.user.jid.isDomainBareJid
        val isGroupchat = ChatManager.getInstance().getOrCreateChat(contact.account, contact.user)
                .isGroupchat
        return if (isGroupchat || isServer) View.VISIBLE else View.GONE
    }

    private fun getGroupchatIndicator(): Int {
        val isServer = contact.user.jid.isDomainBareJid
        val isGroupchat = ChatManager.getInstance().getOrCreateChat(contact.account, contact.user)
                .isGroupchat

        return if (isServer) R.drawable.ic_server_14_border else R.drawable.ic_groupchat_14_border
    }

    private fun getNotificationMuteIcon(): Int?{
        val chat = ChatManager.getInstance().getOrCreateChat(contact.account, contact.user)
        val isCustomNotification = CustomNotifyPrefsManager.getInstance()
                .isPrefsExist(Key.createKey(contact.account, contact.user))
        val mode = chat.notificationState.determineModeByGlobalSettings()

        when (mode){
            NotificationState.NotificationMode.enabled -> return R.drawable.ic_unmute
            NotificationState.NotificationMode.disabled -> return R.drawable.ic_mute
            NotificationState.NotificationMode.bydefault -> return R.drawable.ic_snooze_mini
        }
        if (isCustomNotification && (mode.equals(NotificationState.NotificationMode.enabled)
                        || mode.equals(NotificationState.NotificationMode.bydefault)))
            return R.drawable.ic_notif_custom
        return null
    }

    private fun getUnreadCount() = ChatManager.getInstance()
            .getOrCreateChat(contact.account, contact.user).unreadMessageCount

    private fun getUnreadCountVisibility() = if (getUnreadCount() > 0) View.VISIBLE else View.GONE

}