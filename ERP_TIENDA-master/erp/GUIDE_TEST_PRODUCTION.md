# Guide de Test - Module de Production

## Comment tester la fonctionnalité de production

### 1. Démarrer l'application

```
bash
cd ERP_TIENDA-master/erp
./mvnw spring-boot:run
```

Puis ouvrez : http://localhost:8080

---

### 2. Créer des matières premières (produits simples)

Allez dans **Productos** → **Nuevo producto**

Créez ces produits (type: Simple):
| Nom | Prix | Stock initial |
|-----|------|---------------|
| Vis | 0.10€ | 100 |
| Bois | 5.00€ | 50 |
| Plastique | 2.00€ | 30 |
| Metal | 3.00€ | 40 |

---

### 3. Créer un composant (produit composé)

Allez dans **Productos** → **Nuevo produit**

Créez un produit intermédiaire:
- **Nom**: "Cadre en bois"
- **Prix**: 25.00€
- **Stock**: 0
- **Tipo**: **"Compuesto (con escandallo)"**

Cliquez sur **"Gestionar escandallo"** et ajoutez:
- Bois: 4 unités
- Vis: 8 unités

---

### 4. Créer un produit fini

Allez dans **Productos** → **Nouveau produit**

Créez le produit final:
- **Nom**: "Mueble-kit"
- **Prix**: 80.00€
- **Stock**: 0
- **Tipo**: **"Compuesto (con escandallo)"**

Cliquez sur **"Gestionar escandallo"** et ajoutez:
- Cadre en bois: 1 unité
- Plastique: 2 unités
- Vis: 4 unités

---

### 5. Exécuter une fabrication

Allez dans **Producción** → **Nueva fabricación**

1. Sélectionnez le produit: **"Mueble-kit"**
2. Unités: **5**
3. Date: (aujourd'hui)
4. Employé: (sélectionnez un employé)
5. Cliquez sur **"Ejecutar fabricación"**

---

### 6. Vérifier les résultats

Après la fabrication, vérifiez:

| Produit | Stock avant | Stock après |
|---------|-------------|-------------|
| Vis | 100 | 100 - (4×5) = 80 |
| Bois | 50 | 50 - (4×5) = 30 |
| Plastique | 30 | 30 - (2×5) = 20 |
| Cadre en bois | 0 | 0 - (1×5) = -5 ❌ ERREUR! |

**Problème détecté**: Le stock du composant "Cadre en bois" devient négatif car on n'a pas assez de composants en stock!

---

### 7. D'abord fabriquer le composant

1. Allez dans **Producción** → **Nueva fabricación**
2. Produit: **"Cadre en bois"**
3. Unités: **10**
4. Cliquez sur **"Ejecutar fabrication"**

Vérifiez maintenant:
- Bois: 30 - (4×10) = 10
- Vis: 80 - (8×10) = 0
- Cadre en bois: 0 + 10 = 10

Maintenant vous pouvezfabiquer le produit fini!

---

### 8. Vérifier la fabrication

Allez dans **Producción** (liste) pour voir:
- La date de fabrication
- Le produit fabricadoé
- Les unités
- L'employé

---

## Résumé du processus

```
Matières premières → [FABRICATION] → Composants → [FABRICATION] → Produits finis
     (stock ↓)                                    (stock ↓)          (stock ↑)
```

Le système:
1. ✅ Diminue le stock des composants/matières premières
2. ✅ Augmente le stock du produit fini
3. ✅ Enregistre la date et l'employé
