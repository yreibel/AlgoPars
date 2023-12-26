package algopars.commande;

import algopars.GererFichier;

public class RetourArriere extends Commande
{

	public RetourArriere(String nom, String desc)
	{
		super(nom, desc);
	}
	
	public int executer(GererFichier metier, int ligneAInterpreter, int ligneEnCours, String arguments)
	{
		return metier.interpreterJusqua(ligneEnCours-1);
	}

}
