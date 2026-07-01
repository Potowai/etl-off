DESIGN PATTERNS – TP STATE

OBJECTIF

●  Mettre en place le pattern State

INSTRUCTIONS

●  Dans le projet java-design-patterns
●  Créer un package state
●  Créer une classe Commande :

●  Les états possibles sont les suivants :

o  CREATION
o  PAIEMENT
o  EN_LIVRAISON
o  ANNULEE

●  Créer les méthodes suivantes :

o  ajouterProduit(Produit p) :

o  permet d’ajouter un produit
o  possible uniquement si la commande est dans l’état CREATION

o  payer()

o  calcule le prix total de la commande (pour simplifier ou fera le

nombre de produits * 0.5€)

o  possible uniquement si la commande est dans l’état CREATION
o  passe la commande au statut PAIEMENT.

o

livrer(String adresse)

o  Permet de renseigner l’adresse de livraison
o  Possible uniquement si la commande est dans l’état PAIEMENT
o  Passe la commande au statut EN_LIVRAISON

o  Annuler()

o  Passe la commande au statut ANNULEE
o  Possible uniquement si la commande est au statut CREATION ou

PAIEMENT.

o  Si la commande est au statut EN_LIVRAISON : renvoie un message

d’erreur indiquant que la commande est déjà en cours de livraison et que
l’annulation est impossible.

1

o  Si la commande est déjà statut ANNULEE : renvoie un message

d’erreur indiquant que la commande a déjà été annulée.

●  Implémenter les différentes méthodes en utilisant le pattern State

COMMITEZ SUR GITHUB

2

