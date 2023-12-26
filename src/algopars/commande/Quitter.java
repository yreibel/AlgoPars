package algopars.commande;

import algopars.AlgoPars;
import algopars.GererFichier;

public class Quitter extends Commande
{

	public Quitter(String nom, String desc)
	{
		super(nom, desc);
	}
	
	public int executer(GererFichier metier, int ligneAInterpreter, int ligneEnCours, String arguments)
	{
		AlgoPars.stopperAffichage();
		System.exit(0);
		return 0;
	}

}
