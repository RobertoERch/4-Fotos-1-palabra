import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Juego extends JFrame implements Runnable,ActionListener{
	private JButton casillas[];
	private JButton letras[];
	private String palabra,minuscula,RES=null;
	private char resultado[];
	private int casi=12;

	
	public static void main(String []args){
		ArrayList l=new ArrayList<String>();
		int cont=0,JUGADAS=10;
		
		try{
			File archivo = new File("archivoU.txt");
			FileReader fr = new FileReader(archivo);
			BufferedReader bw= new BufferedReader(fr);
			String pal="";
			
			for(int i=0;(pal=bw.readLine())!=null;i++){
				l.add(pal);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		int rnd=(int)(Math.random()*l.size());
		
		
		boolean bandera=true;
		Juego juego=new Juego( l.remove(rnd).toString() );
		Thread hilo=new Thread(juego);
		hilo.start();
		
		while(bandera){
			//System.out.println("while");
			SLEEP(100);
			if(juego.getRES()!=null){
				String res=juego.getRES();
				if(res.compareTo(juego.getPalabra())==0){
					cont++;
					if(cont<JUGADAS){
						//System.out.println("pregunta");
						juego.dispose();
						rnd=(int)(Math.random()*l.size());
						juego=new Juego(l.remove(rnd).toString());
						juego.setRES();
						hilo=new Thread(juego);
						hilo.start();
						//System.out.println("HILO2");
					}
					else{
						bandera=false;
					}
				}else{
					juego.notaIncorrecto();
					juego.setRES();
				}
			}
		}
		juego.notaCorrecto();
		juego.dispose();
		
	}
	
	
	public Juego(String palabra){
		this.palabra=palabra;
		minuscula=palabra.toLowerCase();
	}
	public String getPalabra(){
		return this.palabra;
	}
	
	public void iniComponentes(){
		int tamP=palabra.length();
		JPanel panelLetras=new JPanel(new GridLayout(1,tamP));
		JPanel panelOpcion=new JPanel(new GridLayout(2,6));
		
		resultado=new char[tamP];
		letras=new JButton[tamP];
		for(int i=0;i<tamP;++i){
			resultado[i]='_';
			letras[i]=new JButton("_");
			letras[i].setVisible(true);
			letras[i].setEnabled(false);
			letras[i].setActionCommand(""+(i+12));
			letras[i].addActionListener(this);
			//letras[i].setSize((int)(400/tamP),80);
			panelLetras.add(letras[i]);
		}
		
		casillas=new JButton[casi];
		for(int i=0;i<casi;++i){
			casillas[i]=new JButton();
			casillas[i].setVisible(true);
			casillas[i].setEnabled(true);
			casillas[i].setActionCommand(""+i);
			casillas[i].addActionListener(this);
			panelOpcion.add(casillas[i]);
		}
		
		panelOpcion.setBounds(50,500,400,50);
		panelOpcion.setVisible(true);
		panelLetras.setBounds(50,400,400,50);
		panelLetras.setVisible(true);
		
		Imagen ima1=new Imagen(minuscula+"1.jpg");
		Imagen ima2=new Imagen(minuscula+"2.jpg");
		Imagen ima3=new Imagen(minuscula+"3.jpg");
		Imagen ima4=new Imagen(minuscula+"4.jpg");

		ima1.setBounds(100,50,150,150);
		ima2.setBounds(250,50,150,150);
		ima3.setBounds(100,200,150,150);
		ima4.setBounds(250,200,150,150);
		
		this.add(ima1);
		this.add(ima2);
		this.add(ima3);
		this.add(ima4);
		this.add(panelLetras);
		this.add(panelOpcion);
		
		this.setLocation(400,80);
		//this.setLocationRelativeTo(null);
		this.setSize(500,600);
		this.setVisible(true);
		this.setResizable(false);
		
		iniOpciones();
	}
	
	public void iniOpciones(){
		ArrayList l=new ArrayList<Character>();
		int tam=palabra.length(),a='A',azar;
		char pal[]=palabra.toCharArray();
		
		for(int i=0;i<casi;++i){
			if(i<tam){
				l.add(pal[i]);
			}else{
				azar=a+(int)(Math.random()*26);
				l.add((char)azar);
			}
		}
		int pos;
		for(int i=0;i<casi;++i){
			pos=(int)(Math.random()*l.size());
			casillas[i].setText(""+l.get(pos));
			l.remove(pos);
		}
	}
	
	public void run(){
		this.setLayout(null);
		iniComponentes();
		System.out.println("RUN");
	}
	
	public void sigJuego(String cadena){
		this.palabra=cadena;
		minuscula=palabra.toLowerCase();
	}
	
	public void actionPerformed(ActionEvent accion){
		int tam=Integer.parseInt(accion.getActionCommand());
		
		if(tam>11){ //Significa que son los botones superiores
			//System.out.println("superiores");
			buscaInferior(tam-12);
		}else{ //Significa que son los botones inferiores
			//System.out.println("inferiores"+tam);
			buscaSuperior(tam);
		}
	}
	
	public void buscaInferior(int posicion){
		for(int i=0;i<12;++i){
			if(!casillas[i].isEnabled()){
				if(casillas[i].getText().compareTo(""+resultado[posicion])==0){
					casillas[i].setEnabled(true);
					resultado[posicion]='_';
					letras[posicion].setEnabled(false);
					letras[posicion].setText("_");
					i=13;
				}
			}
		}
	}
	public void buscaSuperior(int posicion){
		int i=lleno();
		if(i!=-1){
			char letra=casillas[posicion].getText().charAt(0);
			letras[i].setText(""+letra);
			letras[i].setEnabled(true);
			resultado[i]=letra;
			casillas[posicion].setEnabled(false);
		}
		if(lleno()==-1){
			RES=cadResul();
			//System.out.println("Se envia palabra obtenida al servidor: "+RES);
		}
	}
	public int lleno(){
		for(int i=0;i<palabra.length();++i){
			if(resultado[i]=='_'){
				return i;
			}
		}
		return -1;
	}
	public String cadResul(){
		String cad="";
		for(int i=0;i<resultado.length;++i){
			cad+=resultado[i];
		}
		return cad;
	}
	
	public String getRES(){
		return RES;
	}
	
	public void setRES(){
		RES=null;
	}
	
	public void notaCorrecto(){
		JOptionPane.showMessageDialog(null,"FIN DEL JUEGO...","",JOptionPane.INFORMATION_MESSAGE);
	}
	public void notaIncorrecto(){
		JOptionPane.showMessageDialog(null,"Incorrecto...","",JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void SLEEP(int tiempo){
		try {
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
