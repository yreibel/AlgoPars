package algopars.commande;

import algopars.Donnee;
import algopars.GererFichier;

public class AjouterVariable extends Commande
{

	public AjouterVariable(String nom, String desc)
	{
		super(nom, desc);
	}
	
	public int executer(GererFichier metier, int ligneAInterpreter, int ligneEnCours, String arguments)
	{
		for( Donnee variable : metier.getVariables())
			if ( variable.getNom().equals(arguments.split(" ")[2])) metier.ajouterVariablesASuivre(variable);
		
		return ligneEnCours;
	}
	
}
