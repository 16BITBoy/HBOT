package hbot;

import java.util.*;
import java.util.regex.*;
import com.google.appengine.api.xmpp.JID;

public class Commands
{
	Sender sender;
	UserManager mngUser;
	private static String ModCmds;
	private static String UsrCmds;
	private static ArrayList<String> lstCommands;

	public Commands(Sender sender,UserManager mngUser)
	{
		lstCommands = new ArrayList<String>();
		this.sender=sender;
		this.mngUser=mngUser;
		
		//Lista de comandos soportados
		if(lstCommands.isEmpty())
		{
			lstCommands.add("/salute");
			lstCommands.add("/invite");
			lstCommands.add("/online");
			lstCommands.add("/remove");
			lstCommands.add("/nick");
			lstCommands.add("/source");
			lstCommands.add("/setnick");
			lstCommands.add("/save");
			lstCommands.add("/load");
			lstCommands.add("/help");
			lstCommands.add("/snooze");
			lstCommands.add("/private");
			lstCommands.add("/setmod");
			lstCommands.add("/version");
		}
		
		//Ayuda; Comandos de los moderadores
		ModCmds = "/invite <email>     Invita un usuario al grupo\r\n"+
				"/online              Muestra la lista de usuarios en el grupo\r\n"+
				"/remove <Nick>       Remueve un usuario del grupo\r\n"+
				"/nick <Nick>         Cambia el nick actual\r\n"+
				"/setnick <Old> <New>     Cambia el nick de un usuario en especifico por otro\r\n"+
				"/save                Guarda la lista de usuarios en el grupo\r\n"+
				"/load                Carga la lista de usuarios del grupo\r\n"+
				"/setmod <email>      Hace moderador a un usuario\r\n";
		
		//Ayuda; Comandos 
		UsrCmds = "/help              Muestra la ayuda del Bot\r\n"+
				"/salute              Saluda a la comunidad\r\n"+
				"/source              Muestra la direccion del sourcecode del bot\r\n"+
				"/snooze <on/off>     Activa y Desactiva la recepcion de mensajes\r\n"+
				"/private <NickTo> <msg> Manda un mensaje privado al usuario indicado\r\n"+
				"/version             Muestra la versión actual del bot\r\n";
	}

	//Comprueba si el mensaje es un comando
	boolean isCommand(String msg)
	{
		String[] command = MessageUtils.clean(msg);
		return lstCommands.contains(command[0]);
	}

	//Selecciona y ejecuta el comando
	int run(User UserFrom,String msg) throws Exception
	{	
		String[] strArgs = MessageUtils.clean(msg);
		
		ArrayList<String> args = new ArrayList<String>(Arrays.asList(strArgs));
		args.remove(0);
		
		switch(lstCommands.indexOf(strArgs[0]))
		{
			case 0: //salute; Saluda a los usuarios
				Salute();
			break;
			
			case 1: //invite; Invita a un usuario
				if(UserFrom.isMod())
				{
					Invite(args);
					Save();
				}
				else
				{
					PrintNoAccess(UserFrom);
				}
			break;

			case 2: //online; Muestra la lista de usuarios
				Online(UserFrom);
			break;

			case 3: //remove; Elimina a un usuario
				if(UserFrom.isMod())
					Remove(args);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 4: //nick; Cambia el nick del remitente
				if(UserFrom.isMod())
					ChangeNick(UserFrom,args);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 5: //source; Muestra una URL al código del bot
				ShowSource(UserFrom);
			break;

			case 6: //setnick; Modifica el nick de un usuario
				if(UserFrom.isMod())
					SetNick(args);
				else
					PrintNoAccess(UserFrom);
				Save();
			break;

			case 7: //save; Guarda el estado del bot
				if(UserFrom.isMod())
					Save();
				else
					PrintNoAccess(UserFrom);
			break;

			case 8: //load; Carga el estado del bot
				if(UserFrom.isMod())
					Load();
				else
					PrintNoAccess(UserFrom);
			break;
			
			case 9: //help; Muestra la ayuda de comandos del bot
				Help(UserFrom);
			break;

			case 10: //snooze; Marca a un usuario como ausente
				if (args.get(0).compareTo("on")==0)
				{	
					UserFrom.SetSnooze(true);
				}
				else if(args.get(0).compareTo("off")==0)
				{
					UserFrom.SetSnooze(false);
				}
				else
				{
					sender.SendTo(UserFrom, "[BOT] Uso: /snooze <on/off>");
				}
				Save();
			break;

			case 11: //private; Manda un mensage a un usuario conectado
				Private(UserFrom,args);
			break;
			
			case 12: //setmod; Da privilegios de moderador a un usuario
				if(UserFrom.isMod())
				{
					SetMod(args);
					Save();
				}
				else
				{
					PrintNoAccess(UserFrom);
				}
			break;
			
			case 13: //version; Envía la versión actual del bot
				Version(UserFrom);
			break;
		}

		return 0;
	}

	//Saluda a todos los conectados
	int Salute()
	{
		sender.sendEverybody("[BOT] Hola, soy un bot :P");
		return 0;
	}

	//Muestra una URL al código del bot
	int ShowSource(User UserTo)
	{
		sender.SendTo(UserTo,"[BOT] Código disponible en < https://github.com/hzeroo/HBOT >");
		return 0;
	}

	//Muestra un mensaje informativo de que el comando no está accesible para el usuario
	int PrintNoAccess(User user)
	{
		sender.SendTo(user,"[BOT] No tienes permisos para hacer esta operación.");
		return 0;
	}
	
	@SuppressWarnings("deprecation")
	//Muestra la lista de usuarios
	int Online(User UserFrom)
	{
		String lstUsuarios="";

		//Recorremos todos los usuarios
		for(User u:mngUser.getUsers())
		{
			//Comprobamos si es moderador o no
			if(u.isMod())
				lstUsuarios+="[+]";
			else
				lstUsuarios+="[-]";

			lstUsuarios+="["+u.getNick()+"]\t";
			
			//Si el que pide la lista no es moderador, no se muestran los mails
			if(UserFrom.isMod())lstUsuarios+="<"+u.getAddr()+">\t";
			
			if(sender.xmpp.getPresence(new JID(u.getAddr())).isAvailable())lstUsuarios+="conectado";
			lstUsuarios+="\n";
		}
		
		//Envía la lista al usuario que la pidió
		sender.SendTo(UserFrom,"[BOT]\n"+lstUsuarios);
		return 0;
	}

	//Invita y añade a la lista a un usuario
	int Invite(ArrayList<String> args)
	{
		if(args.size()!=1) return -1;
		
		//Comprobamos que el parámetro sea un email 
		Pattern email = Pattern.compile("^\\S+@\\S+\\.\\S+$");
		Matcher mt=email.matcher(args.get(0));
		if(mt.find())
		{	
			//Mandamos la invitación al usuario
			sender.Invite(args.get(0).trim());
			
			//Añadimos el usuario a la lista
			User nUser=new User(new JID(args.get(0).trim()+"/"));
			if(mngUser.addUser(nUser)==0)
			{
				sender.sendEverybody("[BOT] "+nUser.getAddr()+" ha sido invitado.");
			}
			else
			{
				sender.sendEverybody("[BOT] "+nUser.getAddr()+" ya existe.");
			}
		}
		return 0;
	}

	//Elimina un usuario de la lista
	int Remove(ArrayList<String> args)
	{
		if(args.size()!=1) return -1;

		//Comprobamos que el campo de email sea correcto
		Pattern email = Pattern.compile("^\\S+@\\S+\\.\\S+$");
		Matcher mt=email.matcher(args.get(0).trim());
		if(mt.find())
		{
			//Eliminamos al usuario
			if(mngUser.removeUser(args.get(0).trim())==0)
			{
				//Guardamos el estado del bot
				DataManager DM = new DataManager(mngUser);
				DM.remove(args.get(0).trim());
				sender.sendEverybody("[BOT] "+args.get(0).trim()+" eliminado.");
			}
			else
			{
				sender.sendEverybody("[BOT] Error al eliminar al usuario "+args.get(0).trim()+".");
			}
		}

		return 0;
	}

	//Cambia el nick al usuario remitente
	int ChangeNick(User user,ArrayList<String> args)
	{
		if(args.size()!=1) return -1;
		if(args.get(0).trim().contains("@")) return -1;
		
		String oldNick=user.getNick();
		user.setNick(args.get(0).trim());
		sender.sendEverybody("[BOT] "+oldNick+" es ahora conocido como "+user.getNick());
		return 0;
	}

	//Guarda el estado del bot
	int Save() throws Exception
	{
		DataManager DM=new DataManager(mngUser);
		DM.Save();

		return 0;
	}

	//Carga el estado del bot
	int Load() throws Exception
	{
		DataManager DM=new DataManager(mngUser);
		DM.Load();

		return 0;
	}

	//Cambia el nick de un usuario
	int SetNick(ArrayList<String> args) throws Exception
	{
		if(args.size()!=2) return -1;
		if(args.get(1).trim().contains("@")) return -1;

		//Buscamos al usuario
		User u=mngUser.getUser(args.get(0).trim());
		//Si es moderador no permitimos el cambio
		if(!u.isMod())
		{
			//Cambiamos el nick
			String oldNick=u.getNick();
			u.setNick(args.get(1).trim());
			sender.sendEverybody("[BOT] "+oldNick+" es ahora conocido como "+u.getNick());
		}
		return 0;
	}

	//Muestra la ayuda del bot
	int Help(User UserFrom)
	{
		String strHelp = UsrCmds;
		if(UserFrom.isMod()) strHelp+=ModCmds;
		sender.SendTo(UserFrom,strHelp);
		
		return 0;
	}

	//Envía un mensaje privado a un usuario
	int Private(User From,ArrayList<String> args)
	{
		if(args.size()!=2) return -1;
		
		String Msg = "Mensage privado de "+From.getNick()+":\n"+args.get(1);
		sender.SendTo(From,args.get(0),Msg);
		return 0;
	}
	
	//Concede permisos de moderación a un usuario
	int SetMod(ArrayList<String> args) throws Exception
	{
		if(args.size()!=1) return -1;
		
		User u=mngUser.getUser(args.get(0));
		if(!u.isMod())
		{
			u.SetMod(true);
			sender.sendEverybody("[BOT] "+u.getNick()+" es ahora moderador.\r\n");
		}
		
		return 0;
	}
	
	//Muestra la versión actual del BOT
	int Version(User From)
	{
		sender.SendTo(From,"[BOT] Soy la versión 1.0\r\n");
		return 0;
	}
}
