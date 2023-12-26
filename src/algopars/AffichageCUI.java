package algopars;

/**
 * AffichageCUI
 * @version : 1.0,  07 dec. 2018;
 *
 */

import java.util.Scanner;
import org.fusesource.jansi.AnsiConsole;

import algopars.commande.Commande;

import java.util.ArrayList;

public class AffichageCUI
{
	private ArrayList<String> pseudoCode;
	
	public AffichageCUI()
	{
		AnsiConsole.systemInstall();
		pseudoCode = AlgoPars.getPseudoCode();
	}
	
	public void genererAffichage (ArrayList<TraceExec> traceExec, int ligneSelec, String couleurLigne )
	{
		String couleur = "";
		if (couleurLigne.equals("BLANC")) couleur = "\033[30;47m";
		if (couleurLigne.equals("VERT" )) couleur = "\033[37;42m";
		if (couleurLigne.equals("ROUGE")) couleur = "\033[37;101m";
		
		ArrayList<Donnee>   variables = AlgoPars.getVariablesASuivre();
		ArrayList<Commande> commandes = AlgoPars.getListeCommandes();
		
		String sRet = "\033[37;40m\033[H\033[2J\n";
		
		sRet += "[  CODE  ]                                                                        \t[  VARIABLES  ]                                           \n";
		sRet += "+--------------------------------------------------------------------------------+\t+--------------------------------------------------------+\n";
		
		int numLigne = 0;
		int etapeVar = 0;
		
		for( String ligne : this.pseudoCode )
		{	
			if( (ligneSelec < 20 && numLigne < 30) || (ligneSelec >= this.pseudoCode.size()-10 && numLigne >= this.pseudoCode.size()-30) || (numLigne > ligneSelec-20 && numLigne < ligneSelec+11))
			{
				// AFFICHAGE DU CODE
				
				if ( numLigne == ligneSelec ) sRet += "|" + couleur + colorerTexte(String.format( "%3d", numLigne ) + " " + String.format( "%-76s", ligne ) + "\033[37;40m|", couleur);
				
				else sRet+= colorerTexte(String.format( "|%3d", numLigne ) + " " + String.format( "%-76s", ligne ) + "\033[37;40m|", "\033[40m");
	
				// AFFICHAGE DES VARIABLES ET DES COMMANDES
				switch (etapeVar)
				{
					case 0 :
						sRet += "\t|    NOM    |    TYPE    |            VALEUR             |";
						etapeVar++;
						break;
						
					case 1 :
						sRet += "\t+-----------+------------+-------------------------------+";
						etapeVar++;
						break;
						
					case 2 :
						if ( !variables.isEmpty() )
						{
							sRet += "\t| " + String.format( "%-10s", variables.get(0).getNom()       );
							sRet += "| "   + String.format( "%-11s", variables.get(0).getType()      );
							String valeur = variables.remove(0).getValeur();
							if (valeur.equals("" + (char) 0)) valeur = " ";
							if (valeur.equals("null")) valeur  = "VIDE";
							if (valeur.equals("true")) valeur  = "VRAI";
							if (valeur.equals("false")) valeur = "FAUX";
							sRet += "| "   + String.format( "%-30s", (valeur.equals("" + (char) 0)?" ":valeur)) + "|";
						}
						else
						{
							sRet += "\t+-----------+------------+-------------------------------+";
							etapeVar++;
						}
						break;
						
					case 3 :
						etapeVar++;
						break;
					
					case 4 :
						sRet += "\t[  COMMANDES  ]                                           ";
						etapeVar++;
						break;
						
					case 5 :
						sRet += "\t+--------------------------------------------------------+";
						etapeVar++;
						break;
						
					case 6 :
						if ( !commandes.isEmpty() )
						{
							sRet += "\t| " + String.format( "%-8s" , commandes.get(0).getNom()      ) + ": ";
							sRet += String.format( "%-45s", commandes.remove(0).getDesc()  ) + "|";
						}
						else
						{
							sRet += "\t+--------------------------------------------------------+";
							etapeVar++;
						}
						break;
						
					default : break;	
				}
				sRet += "\n";
			}
			numLigne++;
		}

		sRet += "+--------------------------------------------------------------------------------+\n";
		
		sRet += "\n";
		sRet += "[  CONSOLE  ]                                                                     \n";
		sRet += "+--------------------------------------------------------------------------------+\n";
		
		for(int cpt = 0; cpt < 3; cpt++)
		{	
			String print;
			
			if(traceExec.size() > cpt)
			{
				print = traceExec.get(traceExec.size()>3?traceExec.size()-3+cpt:cpt).toString();
				
				if (traceExec.get(traceExec.size()>3?traceExec.size()-3+cpt:cpt).estEntreeClavier())
				{
					print = "\033[4m" + print + "\033[0;40m";
					sRet+= "| " + String.format( "%-79s", print ) + "           |\n";
				}
				else sRet+= "| " + String.format( "%-79s|", print ) + "\n";
			}
			else sRet+= "| " + String.format( "%-79s|", " " ) + "\n";
		}
		
		sRet += "| >                                                                              |\n";
		sRet += "+--------------------------------------------------------------------------------+\n";
		sRet += String.format("%c[%dA",0x1B,2);
		sRet += String.format("%c[%dC",0x1B,4);

		System.out.print( sRet );
	}
	
	private static String colorerTexte ( String texte, String couleurFond )
	{
		String[] expressionComposee = { "sinon ", "fsi ", "si ", " alors", "ftq ", "tq ", " faire" };
		
		String[] expressionSimple = {"ecrire", "lire"};
		
		for (String mot : expressionComposee)
			texte = texte.replaceAll( mot, "\033[1;96m" + mot + "\033[0;37m" + couleurFond );
		
		for (String mot : expressionSimple)
			texte = texte.replaceAll( mot, "\033[1;35m" + mot + "\033[0;37m" + couleurFond );
		
		texte = texte.replaceAll ( "//", "\033[32m//");
		
		return texte;
	}
	
	public void stopperAffichage()
	{
		String sRet = "";
		
		sRet += String.format("%c[%dA",0x1B,0);
		sRet += String.format("%c[%dC",0x1B,0);
		sRet += "\n\n\033[0m\n";
		
		System.out.println(sRet);
	}
	
	public String demanderVariablesASuivre()
	{
		ArrayList<Donnee> variables = AlgoPars.getVariables();
		
		String sRet = "";
		
		sRet += "Variables detectees dans le code : ";
		
		for(Donnee variable : variables)
				sRet += variable.getNom() + " - ";
		
		sRet = sRet.substring(0, sRet.length()-3) + "\n";
		sRet += "Veuillez choisir les variables a suivre : ";
		System.out.print(sRet);
		
		return demanderEntree();
	}
	
	public String lireVariable(ArrayList<TraceExec> traceExec, int ligneSelec, String couleur)
	{
		this.genererAffichage(traceExec, ligneSelec, couleur);
		
		return demanderEntree();
	}
	
	public String demanderEntree()
	{
		@SuppressWarnings("resource")
		Scanner entree = new Scanner(System.in);
		
		return entree.nextLine();
	}
}
