package algopars;

/**
 * AlgoPars
 * @version : 1.0,  07 dec. 2018;
 *
 */
 
import java.util.ArrayList;

import algopars.commande.Commande;

public class AlgoPars
{
	private static GererFichier metier;
	private static AffichageCUI affichage;
	
	public static void main(String[] args)
	{
		
		if ( args.length == 0 )
		{
			System.out.println("Veuillez preciser un fichier: java -jar AlgoPars.jar nomFichier");
			System.exit(0);
		}
		
		if ( args.length >= 2 && args[1].equals("auto") ) metier = new GererFichier(args[0], true);
		else metier = new GererFichier(args[0]);
		
		affichage = new AffichageCUI();
		
		metier.lancerLecture();
	}
	
	//METHODES METIER
	
	public static ArrayList<Donnee>   getConstantes       () { return metier.getConstantes();       }
	public static ArrayList<Donnee>   getVariables        () { return metier.getVariables();        }
	public static ArrayList<Donnee>   getVariablesASuivre () { return metier.getVariablesASuivre(); }
	public static ArrayList<Commande> getListeCommandes   () { return metier.getListeCommandes();   }
	public static ArrayList<String>   getPseudoCode       () { return metier.getPseudoCode();       }
	
	//METHODES AFFICHAGE
	
	public static void genererAffichage(ArrayList<TraceExec> traceExec, int ligneInterpretee, String couleur)
	{
		affichage.genererAffichage(traceExec, ligneInterpretee, couleur);
	}
	
	public static void stopperAffichage()
	{
		affichage.stopperAffichage();
	}
	
	public static String demanderVariablesASuivre()
	{
		return affichage.demanderVariablesASuivre();
	}
	
	public static String demanderEntree()
	{
		return affichage.demanderEntree();
	}
	
	public static String lireVariable(ArrayList<TraceExec> traceExec, int ligneSelec, String couleur)
	{
		return affichage.lireVariable(traceExec, ligneSelec, couleur);
	}
}
