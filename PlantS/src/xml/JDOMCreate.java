package xml;
import java.io.FileOutputStream;
import java.util.Random;

import javax.vecmath.Vector3f;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
 
public class JDOMCreate
{
	//Nous allons commencer notre arborescence en créant la racine XML
	   //qui sera ici "personnes".
	   static Element racine = new Element("arbre");
	 //On crée un nouveau Document JDOM basé sur la racine que l'on vient de créer
	   static Document document = new Document(racine);
 
 
   public JDOMCreate(String fichier) {
 
	   Attribute length = new Attribute("length","0");
	   racine = new Element("arbre");
	   document = new Document(racine);
	   racine.setAttribute(length);
	   createArbre();
	   enregistre(fichier);
 
   }
 
   public static void createArbre()
   {	
	   float minLength = 15.0f;
	   float maxLength = 20.0f;
	   Random rand = new Random();
	   rand.setSeed(System.currentTimeMillis());
 
	   float lengthTronc = rand.nextFloat()*(maxLength - minLength) + minLength;
	   float maxRad = 0.22f*lengthTronc;
	   float minRad = 1.0f;
	   float radTronc = rand.nextFloat()*(maxRad - minRad) + minRad;
 
	   //On crée un nouvel Element trunck qui correspond au tronc et on l'ajoute
	   //en tant qu'Element de racine
	   Element trunck = new Element("trunck");
	   racine.addContent(trunck);
 
	   //On crée un nouvel Attribut length et on l'ajoute au tronc
	   //grâce à la méthode setAttribute
	   Attribute length = new Attribute("length",""+lengthTronc+"");
	   trunck.setAttribute(length);
	   Attribute radius = new Attribute("radius",""+radTronc+"");
	   trunck.setAttribute(radius);
	   Attribute axe = new Attribute("axe","0 1 0");
	   trunck.setAttribute(axe);
 
	   //Ajout feuille ?
 
	   float tirage = rand.nextFloat();
	   boolean twoSons;
	   if(tirage > (float)1/(float)4) {
		   twoSons = true;
	   } else {
		   twoSons = false;
	   }
 
	   createBranch(trunck, lengthTronc, new Vector3f(0,1,0), radTronc, twoSons);
 
 
      //On crée un nouvel Element nom, on lui assigne du texte
      //et on l'ajoute en tant qu'Element de etudiant
      //Element nom = new Element("nom");
      //trunck.addContent(nom);
   }
 
   static void createBranch(Element trunck, float Tp, Vector3f axe, float Rp, boolean deuxFils) {
	   Random rand = new Random();
	   rand.setSeed(System.currentTimeMillis());
	   float B;
	   float maxVar = 0.3f;
	   float minVar = 0.0f;
	   float maxPhi = (float) (Math.PI/2);
	   float minPhi = (float) (Math.PI/6);
	   float maxTheta = (float) (Math.PI/4);
	   float minTheta = (float) -(Math.PI/4);
	   float minl = 2.0f;
	   float maxl = 5.0f;
	   float minh = 2.0f;
	   float maxh = 10.0f;
	   float alphaVar = rand.nextFloat()*(maxVar - minVar) + minVar;
	   if(rand.nextBoolean()) {
		   B = 1;
	   }
	   else {
		   B = -1;
	   }
 
	   float length1 = (0.7f + B*alphaVar)*Tp;
	   alphaVar = rand.nextFloat()*(maxVar - minVar) + minVar;
	   if(rand.nextBoolean()) {
		   B = 1;
	   }
	   else {
		   B = -1;
	   }
	   float radius1 = (0.5f + B*alphaVar)*Rp;
	   float phi = rand.nextFloat()*(maxPhi - minPhi) + minPhi;
	   float theta = rand.nextFloat()*(maxTheta - minTheta) + minTheta;
	   //float r = rand.nextFloat()*(maxl - minl) + minl;
	   //float h = rand.nextFloat()*(maxh - minh) + minh;
 
	   float x = (float) (1.0f*Math.cos(theta)*Math.sin(phi));
	   float y = (float) (5.0f*Math.sin(theta)*Math.sin(phi));
	   float z = (float) (1.0f*Math.cos(phi));
 
	   Vector3f v = new Vector3f(x, y, z);
	   if(v.y < 0.0f) {
		   v.y = (-1)*v.y;
	   }
	   v.normalize();
	   while(v.dot(new Vector3f(1,0,0)) > 0.9) {
		   phi = rand.nextFloat()*(maxPhi - minPhi) + minPhi;
		   theta = rand.nextFloat()*(maxTheta - minTheta) + minTheta;
		   //float r = rand.nextFloat()*(maxl - minl) + minl;
		   //float h = rand.nextFloat()*(maxh - minh) + minh;
 
		   x = (float) (1.0f*Math.cos(theta)*Math.sin(phi));
		   y = (float) (5.0f*Math.sin(theta)*Math.sin(phi));
		   z = (float) (1.0f*Math.cos(phi));
 
		   v = new Vector3f(x, y, z);
		   if(v.y < 0.0f) {
			   v.y = (-1)*v.y;
		   }
		   v.normalize();
	   }
 
	   Element son1 = new Element("trunck");
	   trunck.addContent(son1);
 
	   //On crée un nouvel Attribut length et on l'ajoute au tronc
	   //grâce à la méthode setAttribute
	   Attribute length = new Attribute("length",""+length1+"");
	   son1.setAttribute(length);
	   Attribute radius = new Attribute("radius",""+radius1+"");
	   son1.setAttribute(radius);
	   Attribute newaxey = new Attribute("axe",""+v.x+" "+v.y+" "+v.z+"");
	   son1.setAttribute(newaxey);
 
	   addLeaf(son1, length1, radius1);
 
	   float tirage = rand.nextFloat();
 
	   boolean twoSons;
	   if(tirage > 0.2f) {
		   twoSons = true;
	   } else {
		   twoSons = false;
	   }
	   //Conditions d'arret de l'algo de génération.
	   if((length1 > 1f) && (radius1 > 0.1f)) {
		   createBranch(son1, length1, v, radius1, twoSons);
	   } else {
 
	   }
//-------------------------------------------------- 2 FILS -------------------------------------------------------------//	   
	   if(deuxFils) {
		   maxTheta = (float) (5.0f*Math.PI/4.0f);
		   minTheta = (float) (3.0f*Math.PI/4.0f);
 
		   if(rand.nextBoolean()) {
			   B = 1;
		   }
		   else {
			   B = -1;
		   }
 
		   alphaVar = rand.nextFloat()*(maxVar - minVar) + minVar;
		   float length2 = (0.7f + B*alphaVar)*Tp;
		   float radius2 = (float) Math.sqrt(Rp*Rp - radius1*radius1);
		   phi = rand.nextFloat()*(maxPhi - minPhi) + minPhi;
 
		   phi = rand.nextFloat()*(maxPhi - minPhi) + minPhi;
		   //theta = rand.nextFloat()*(maxTheta - minTheta) + minTheta;
		   //float r = rand.nextFloat()*(maxl - minl) + minl;
		   //float h = rand.nextFloat()*(maxh - minh) + minh;
		   theta = (float) -theta;
 
		   x = (float)(1.0f*Math.cos(theta)*Math.sin(phi));
		   y = (float)(5.0f*Math.sin(theta)*Math.sin(phi));
		   z = (float)(1.0f*Math.cos(phi));
		   alphaVar = rand.nextFloat()*(maxVar - minVar) + minVar;
		   v.x = (-1)*v.x;
		   v.z = (-1)*v.z;
		   v.x = v.x + alphaVar;
		   alphaVar = rand.nextFloat()*(maxVar - minVar) + minVar;
		   v.z = v.z + alphaVar;
 
		   /*if(v.y < 0.0f) {
			   v.y = (-1)*v.y;
		   }*/
		   v.normalize();
 
		   Element son2 = new Element("trunck");
		   trunck.addContent(son2);
 
		   //Ajout d'attribut
		   length = new Attribute("length",""+length2+"");
		   son2.setAttribute(length);
		   radius = new Attribute("radius",""+radius2+"");
		   son2.setAttribute(radius);
		   newaxey = new Attribute("axe",""+v.x+" "+v.y+" "+v.z+"");
		   son2.setAttribute(newaxey);
 
		   addLeaf(son2, length2, radius2);
 
		   tirage = rand.nextFloat();
		   if(tirage > (float)1/(float)4) {
			   twoSons = true;
		   } else {
			   twoSons = false;
		   }
		   // Conditions d'arret de l'algo de génération.
		   if((length2 > 1f) && (radius2 > 0.1f)) {
			   createBranch(son2, length2, v, radius2, twoSons);
		   } else {
 
		   }
	   }
   }
   public static void addLeaf(Element trunck, float lengthBranch, float radiusBranch) {
	   Random rand = new Random();
	   rand.setSeed(System.currentTimeMillis());
	   float minNbLeaf = 25.0f;
	   float maxNbLeaf =30.0f;
	   float nbFeuille = rand.nextFloat()*(maxNbLeaf - minNbLeaf) + minNbLeaf;
	   float minAngle = 0.0f;
	   float maxAngle = 360.0f;
	   float minPhi = 0f;
	   float maxPhi = 180f;
	   float minPercent = 20.0f;
	   float maxPercent = 100.0f;
	   float minScale = 0.8f;
	   float maxScale = 1.2f;
	   float scale;
	   float pourcentageHauteur;
	   float angleSurDiscLat;
	   float rho;
	   float theta;
	   float phi;
	   int IDtype;
 
	   for(int i = 0; i < nbFeuille; ++i) {
		   rho = rand.nextFloat()*(maxAngle - minAngle) + minAngle;
		   theta = rand.nextFloat()*(maxAngle - minAngle) + minAngle;
		   pourcentageHauteur = rand.nextFloat()*(maxPercent - minPercent) + minPercent;
		   scale = rand.nextFloat()*(maxScale - minScale) + minScale;
		   IDtype = rand.nextInt(2);
		   angleSurDiscLat = rand.nextFloat()*(maxAngle - minAngle) + minAngle;
		   phi = rand.nextFloat()*(maxPhi - minPhi) + minPhi;
		   if(angleSurDiscLat >= 270 || angleSurDiscLat <= 90) {
			   phi = -phi;
		   }
		   
		   
		   Element leaf = new Element("leaf");
		   trunck.addContent(leaf);
		   Attribute IDTYPE = new Attribute("type", ""+IDtype);
		   leaf.setAttribute(IDTYPE);
		   Attribute RHO = new Attribute("rho",""+rho+"");
		   leaf.setAttribute(RHO);
		   Attribute THETA = new Attribute("theta",""+theta+"");
		   leaf.setAttribute(THETA);
		   Attribute PHI = new Attribute("phi",""+phi+"");
		   leaf.setAttribute(PHI);
		   Attribute ANGLESURDISCLAT = new Attribute("angleDiscLat",""+angleSurDiscLat+"");
		   leaf.setAttribute(ANGLESURDISCLAT);
		   Attribute HAUTEUR = new Attribute("height",""+pourcentageHauteur+"");
		   leaf.setAttribute(HAUTEUR);
		   Attribute SCALE = new Attribute("scale",""+scale+"");
		   leaf.setAttribute(SCALE);
		   Attribute LENGTHCOURANT = new Attribute("lengthCurrent",""+lengthBranch+"");
		   leaf.setAttribute(LENGTHCOURANT);
		   Attribute RADIUSCOURANT = new Attribute("radiusCurrent",""+radiusBranch+"");
		   leaf.setAttribute(RADIUSCOURANT);
	   }
 
   }
      static void affiche() {
    		  try
    		  {
    		  //On utilise ici un affichage classique avec getPrettyFormat()
    		  XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
    		  sortie.output(document, System.out);
    		  }
    		  catch (java.io.IOException e){}
      }
      static void enregistre(String fichier)
      {
         try
         {
            //On utilise ici un affichage classique avec getPrettyFormat()
            XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
            //Remarquez qu'il suffit simplement de créer une instance de FileOutputStream
            //avec en argument le nom du fichier pour effectuer la sérialisation.
            sortie.output(document, new FileOutputStream(fichier));
         }
         catch (java.io.IOException e){}
      }
   }