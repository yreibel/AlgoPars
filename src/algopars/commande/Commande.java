package algopars.commande;

import java.util.ArrayList;

import algopars.GererFichier;

public abstract class Commande
{	
	private String nom;
	private String desc;
	
	public Commande(String nom, String desc)
	{
		this.nom  = nom;
		this.desc = desc;
	}
	
	public String getNom () { return this.nom;  }
	public String getDesc() { return this.desc; }
	
	public abstract int executer(GererFichier metier, int ligneAInterpreter, int ligneEnCours, String arguments);
	
	public static ArrayList<Commande> preparerCommandes()
	{
		ArrayList<Commande> listeCommande = new ArrayList<Commande>();
		
		listeCommande.add(new RetourArriere    ("b"       , "retourne a la ligne precedente"));
		listeCommande.add(new SelectionLigne   ("l"       , "va a la ligne en parametre"));
		listeCommande.add(new Quitter          ("q"       , "quitte le programme"));
		listeCommande.add(new AjouterVariable  ("+ var"   , "ajoute la variable en parametre"));
		listeCommande.add(new SupprimerVariable("- var"   , "supprimme la variable en parametre"));
		listeCommande.add(new CopierTraceExec  ("cp trace", "copie la trace des variables en parametre"));
		
		return listeCommande;
		
	}
}
