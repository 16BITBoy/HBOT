﻿package hbot;

import java.util.*;

public class UserManager
{
	private ArrayList<User> lstUsers;
	private int ID=0;
	
	public UserManager()
	{
		lstUsers=new ArrayList<User>();
	}
	
	public int getID()
	{
		return this.ID;
	}
	
	public int addUser(User user)
	{
		for(User u:this.getUsers())
		{
			if(u.getAddr().compareTo(user.getAddr())==0) return -1;
		}
		
		lstUsers.add(user);
		
		return 0;
	}
	
	public int removeUser(String Addr)
	{
		for(int i=0;i<this.lstUsers.size();i++)
		{
			if(lstUsers.get(i).getAddr().compareTo(Addr)==0)
			{
				if(!lstUsers.get(i).isMod())
				{
					this.getUsers().remove(i);
					return 0;
				}
				else return -1;
			}
		}
		
		return -1;
	}

	public List<User> getUsers()
	{
		return lstUsers;
	}
	public User getUser(String NickOrEMail)throws Exception{
		boolean IsNick = (NickOrEMail.indexOf('@')==-1)?true:false;
		for(int i = 0;i<lstUsers.size();i++){
			if(IsNick){
				if(lstUsers.get(i).getNick().compareTo(NickOrEMail)==0)return lstUsers.get(i);
			}else{
				if(lstUsers.get(i).getAddr().compareTo(NickOrEMail)==0)return lstUsers.get(i);
			}
		}
		throw new Exception();
	}
}
