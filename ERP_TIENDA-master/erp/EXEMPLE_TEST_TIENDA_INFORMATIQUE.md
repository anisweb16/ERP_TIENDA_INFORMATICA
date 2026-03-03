# Exemple concret : Tienda Informática
## Guide de test complet du module de production

---

## 🎯 Exemple choisi : Assembly d'un PC Gaming

Dans une tienda informática, nous allons créer :
- **PC Gaming GamerPro** (produit fini)
- **Unité Centrale** (composant)
- **Périphériques** (composants)
- **Pièces de base** (matières premières)

---

## 📋 ÉTAPE 1 : Créer les matières premières

Allez dans **Productos** → **Nuevo producto** et créez :

| Nom | Prix | Stock | Type |
|-----|------|-------|------|
| Processeur Intel i5 | 150€ | 20 | Simple |
| Carte graphique RTX 3060 | 300€ | 15 | Simple |
| Mémoire RAM 16GB | 60€ | 30 | Simple |
| Disque SSD 500GB | 45€ | 25 | Simple |
| Alimentation 650W | 50€ | 20 | Simple |
| Boîtier PC | 40€ | 15 | Simple |
| Clavier USB | 15€ | 50 | Simple |
| Souris Wireless | 12€ | 50 | Simple |
| Écran 24" | 120€ | 20 | Simple |

---

## 📋 ÉTAPE 2 : Créer un COMPOSANT (Unité Centrale)

Créez un produit composé intermédiaire :

### 2.1 Création du produit
- **Nom** : Unité Centrale Gamer
- **Prix** : 500€
- **Stock** : 0
- **Tipo** : **Compuesto (con escandallo)**

### 2.2 Définir l'escandallo
Cliquez sur **"Gestionar escandallo"** et ajoutez :

| Composant | Quantité | Type | Coût |
|-----------|----------|------|------|
| Processeur Intel i5 | 1 | P | 150€ |
| Carte graphique RTX 3060 | 1 | P | 300€ |
| Mémoire RAM 16GB | 1 | P | 60€ |
| Disque SSD 500GB | 1 | P | 45€ |
| Alimentation 650W | 1 | P | 50€ |
| Boîtier PC | 1 | P | 40€ |

---

## 📋 ÉTAPE 3 : Créer le PRODUIT FINI (PC Gaming)

### 3.1 Création du produit
- **Nom** : PC Gaming GamerPro
- **Prix** : 1200€
- **Stock** : 0
- **Tipo** : **Compuesto (con escandallo)**

### 3.2 Définir l'escandallo
Cliquez sur **"Gestionar escandallo"** et ajoutez :

| Composant | Quantité | Type | Coût |
|-----------|----------|------|------|
| Unité Centrale Gamer | 1 | P | 645€ |
| Clavier USB | 1 | P | 15€ |
| Souris Wireless | 1 | P | 12€ |
| Écran 24" | 1 | P | 120€ |

---

## 📋 ÉTAPE 4 : FABRIQUER d'abord le COMPOSANT

On ne peut pasfabricquer directement le PC car l'unité centrale n'a pas de stock!

### 4.1 Exécuter la fabrication
Allez dans **Producción** → **Nueva fabricación**

```
Produit : Unité Centrale Gamer
Unités : 5
Date : 2024-01-15
Employé : [Sélectionnez un employé]
```

Cliquez **"Ejecutar fabricación"**

### 4.2 Résultat attendu
| Produit | Stock AVANT | Stock APRÈS |
|---------|-------------|------------|
| Processeur Intel i5 | 20 | 20 - (1×5) = **15** |
| Carte graphique RTX 3060 | 15 | 15 - (1×5) = **10** |
| Mémoire RAM 16GB | 30 | 30 - (1×5) = **25** |
| Disque SSD 500GB | 25 | 25 - (1×5) = **20** |
| Alimentation 650W | 20 | 20 - (1×5) = **15** |
| Boîtier PC | 15 | 15 - (1×5) = **10** |
| **Unité Centrale Gamer** | 0 | 0 + 5 = **5** ✓ |

---

## 📋 ÉTAPE 5 : FABRIQUER le PRODUIT FINI

### 5.1 Exécuter la fabrication
Allez dans **Producción** → **Nueva fabricación**

```
Produit : PC Gaming GamerPro
Unités : 3
Date : 2024-01-15
Employé : [Sélectionnez un employé]
```

Cliquez **"Ejecutar fabricación"**

### 5.2 Résultat attendu
| Produit | Stock AVANT | Stock APRÈS |
|---------|-------------|------------|
| Unité Centrale Gamer | 5 | 5 - (1×3) = **2** |
| Clavier USB | 50 | 50 - (1×3) = **47** |
| Souris Wireless | 50 | 50 - (1×3) = **47** |
| Écran 24" | 20 | 20 - (1×3) = **17** |
| **PC Gaming GamerPro** | 0 | 0 + 3 = **3** ✓ |

---

## 📋 ÉTAPE 6 : Vérifier les enregistrements

Allez dans **Producción** pour voir :

| Fecha | Producto | Unidades | Empleado |
|-------|----------|----------|----------|
| 2024-01-15 | Unité Centrale Gamer | 5 | Jean |
| 2024-01-15 | PC Gaming GamerPro | 3 | Jean |

---

## 🔄 Schéma récapitulatif

```
MATIÈRES PREMIÈRES          COMPOSANTS                PRODUITS FINIS
===================          ===========                ==============

Processeur i5 ─────────┐
                       │
Carte graphique ──────┼──→ [FABRICATION] ──→ Unité Centrale ──┐
                       │         -5                          │
RAM 16GB ─────────────┤                                      │
                       │   Stock composants                  │
SSD 500GB ────────────┤         ↓                           │
                       │                                   ├──→ [FABRICATION] ──→ PC Gaming GamerPro
Alimentation ─────────┤                                      │         -3
                       │                                      │    Stock produits finis
Boîtier PC ───────────┘                                      │         ↓
                                                                   │
Clavier ───────────────────────────────────────────────────┘         +3

Souris ──────────────────────────────────────────────────┘
                                                                   
Écran ───────────────────────────────────────────────────┘
```

---

## ✅ Résumé du test

| Action | Composants consommés | Produit créé |
|--------|---------------------|--------------|
| Fabrication 5 Unités Centrales | -5 processeurs, -5 cartes graphiques, etc. | +5 Unités Centrales |
| Fabrication 3 PC Gaming | -3 Unités Centrales, -3 claviers, etc. | +3 PC Gaming |

Le système gère correctement :
- ✅ Stock des matières premières (diminue)
- ✅ Stock des composants (diminue)  
- ✅ Stock des produits finis (augmente)
- ✅ Enregistrement de la date et de l'employé

---

## 🚀 Pour tester maintenant

1. Démarrez l'application : `cd ERP_TIENDA-master/erp && ./mvnw spring-boot:run`
2. Ouvrez http://localhost:8080
3. Suivez les étapes 1 à 6 ci-dessus
4. Vérifiez que les stocks évoluent correctement
