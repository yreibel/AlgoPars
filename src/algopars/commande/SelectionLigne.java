package algopars.commande;

import algopars.GererFichier;

public class SelectionLigne extends Commande
{

	public SelectionLigne(String nom, String desc)
	{
		super(nom, desc);
	}
	
	public int executer(GererFichier metier, int ligneAInterpreter, int ligneEnCours, String arguments)
	{
		return metier.interpreterJusqua(Integer.parseInt(arguments.substring(1).trim()));
	}
	
}
