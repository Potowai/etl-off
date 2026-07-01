DESIGN PATTERNS – TP GRASPS

OBJECTIF

●  Mettre en œuvre les GRASPS en refactorant du code.

CONTEXTE

Le contexte du TP est une application web de type billetterie qui permet de réserver des places pour
des salles de spectacles ou cinémas.

Dans  cette  application  web,  le  contrôleur  ReservationController  récupère  des
informations
provenant  du  front  et  créer  une  instance  de  Reservation  qui  contient  les  diverses  informations
d’origine ainsi que le montant total à payer.

INSTRUCTIONS

●  Commencez par faire un fork du projet suivant : https://github.com/ssy-sdv/java-dp

●  Une fois que vous avez réalisé le fork, clonez le projet et installez-le dans votre IDE préféré.

●  L’exercice vous propose de réaliser le refactoring d’une classe appelée

ReservationController et qui se trouve dans le package fr.sdv.tp_grasps.

●  Que fait cette classe ?

1.  Elle commence par extraire des données transmises par la classe Params

a.

Identifiant du client

b.  Date de réservation au format chaine de caractères

c.  Type de réservation (CI=cinéma, TH=Théâtre)

d.  Nombre de places réservées

2.  Elle convertit la date de réservation de String en LocalDateTime

3.  Elle recherche le client en fonction de son identifiant. Pour cela elle utilise une DAO.

4.  Elle recherche ensuite les informations tarifaires concernant la réservation en

fonction de son type. Pour cela une seconde DAO est utilisée.

5.  L’instance de la classe Reservation est créée.

6.  On ajoute à la classe Client la nouvelle réservation.

7.  Le montant total de la réservation est calculé. Il prend en compte :

1

a.  Si le client est premium ou pas

b.  Le pourcentage de la réduction pour les clients premiums.

●  Important : une classe de tests unitaire permet de vérifier que la classe fonctionne :

ReservationControllerTest. Cette classe pourra être utilisée après votre refactoring afin de

vérifier que tout continue à fonctionner comme avant.

●  Tâches à réaliser :

o  Mettez en œuvre ce que vous avez compris des GRASPS pour proposer un code

utilisant de meilleurs pratiques.

COMMITEZ SUR GITHUB

2

