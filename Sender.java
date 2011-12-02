package hbot;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPService;

public class Sender
{
	UserManager mngUser;
	public XMPPService xmpp;
	
	Sender(XMPPService xmpp,UserManager mngUser)
	{
		this.mngUser=mngUser;
		this.xmpp=xmpp;
	}
	
	//TODO: Actualizar deprecated
	@SuppressWarnings("deprecation")
	//Envia el mensaje a todos los usuarios
	int sendEverybody(String msg)
	{
		  for(User user:mngUser.getUsers())
		  {
			  JID jid=new JID(user.getAddr());
				  if(xmpp.getPresence(jid).isAvailable()&&!user.isSnoozing())
				  {
					  Message message = new MessageBuilder()
			  			.withRecipientJids(jid)
			  			.withBody(msg)
			  			.build();
			  
					  xmpp.sendMessage(message);
				  }
		  }		  
		  return 0;
	}
	
	//TODO: Actualizar deprecated
	@SuppressWarnings("deprecation")
	//Envia el mensaje a todos los usuarios menos al que lo envió
	int SendEveryBodyFrom(User UserFrom,String msg)
	{
		  for(User user:mngUser.getUsers())
		  {
			  if(user.getAddr().compareTo(UserFrom.getAddr())!=0 && !user.isSnoozing())
			  {
				  JID jid=new JID(user.getAddr());
				  if(xmpp.getPresence(jid).isAvailable())
				  {
					  Message message = new MessageBuilder()
			  			.withRecipientJids(jid)
			  			.withBody(msg)
			  			.build();
			  
					  xmpp.sendMessage(message);
				  }
			  }
		  }
		return 0;
	}
	
	//TODO: Actualizar deprecated
	@SuppressWarnings("deprecation")
	//Envia el mensaje a un usuario en concreto
	int SendTo(User UserTo,String msg)
	{
		JID jid=new JID(UserTo.getAddr());
		if(xmpp.getPresence(jid).isAvailable())
		{
			Message message = new MessageBuilder()
			.withRecipientJids(jid)
			.withBody(msg)
			.build();
		  
			xmpp.sendMessage(message);
		}
		  
		return 0;
	}
	//Envia el mensaje a un usuario en concreto, por su nick
	int SendTo(User UserFrom,String NickTo,String msg)
	{
		for(User user:mngUser.getUsers())
		{
			
			  if(user.getNick().compareTo(NickTo)==0)
			  {
				  JID jid=new JID(user.getAddr());
				  Message message = new MessageBuilder()
			  			.withRecipientJids(jid)
			  			.withBody(msg)
			  			.build();
			  
				  xmpp.sendMessage(message);
			  }
		  }
		
		return 0;
	}
	
	//Envía una invitación a una dirección dada
	int Invite(String Addr)
	{
		xmpp.sendInvitation(new JID(Addr));
		return 0;
	}	
}
