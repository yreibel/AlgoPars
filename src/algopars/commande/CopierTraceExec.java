package algopars.commande;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import algopars.Donnee;
import algopars.GererFichier;

public class CopierTraceExec extends Commande
{

	public CopierTraceExec(String nom, String desc)
	{
		super(nom, desc);
	}
	
	public int executer(GererFichier metier, int ligneAInterpreter, int ligneEnCours, String arguments)
	{
		ArrayList<String> listeParam = new ArrayList<String>();
		String[] varChoisies = arguments.split(" ");
		for(int i=2; i<varChoisies.length; i++)
			listeParam.add(varChoisies[i]);
		
		String sRet  = "+-----------+------------+---------------------------+\n";
			   sRet += "|    NOM    |    TYPE    |        VALEUR             |\n";
		       sRet += "+-----------+------------+---------------------------+\n";
		
		for(Donnee d : metier.getVariablesASuivre())
		{
			for(String param : listeParam)
			{
				if(param.equals(d.getNom()))
				{
					sRet += "| "   + String.format( "%-10s", d.getNom()       ) ;
					sRet += "| "   + String.format( "%-11s", d.getType()      ) ;
					sRet += "| "   + String.format( "%-26s", d.getValeur() ) + "|\n";
				}
			}
		}
		sRet += "+-----------+------------+---------------------------+";
			
		StringSelection selection  = new StringSelection(sRet);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		
		return ligneEnCours;
	}

}
