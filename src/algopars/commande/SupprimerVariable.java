package algopars.commande;

import algopars.Donnee;
import algopars.GererFichier;

public class SupprimerVariable extends Commande
{

	public SupprimerVariable(String nom, String desc)
	{
		super(nom, desc);
	}
	
	public int executer(GererFichier metier, int ligneAInterpreter, int ligneEnCours, String arguments)
	{
		Donnee varASuppr = null;
		
		for( Donnee variable : metier.getVariablesASuivre())
			if ( variable.getNom().equals(arguments.split(" ")[2])) varASuppr = variable;
		
		if (varASuppr != null) metier.supprimerVariablesASuivre(varASuppr);
		
		return ligneEnCours;
	}

}
