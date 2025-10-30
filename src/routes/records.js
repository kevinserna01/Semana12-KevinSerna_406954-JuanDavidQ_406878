import { Router } from 'express';
import { listRecords, addRecord } from '../controllers/recordsController.js';

const router = Router();

// GET /api/records -> lista el arreglo completo
router.get('/', async (req, res) => {
  try {
    const records = await listRecords();
    res.json(records);
  } catch (error) {
    res.status(500).json({ message: 'Error al leer los datos' });
  }
});

// POST /api/records -> agrega { nombre, email }
router.post('/', async (req, res) => {
  try {
    const { nombre, email } = req.body;

    if (!nombre || !email) {
      return res.status(400).json({ message: 'nombre y email son requeridos' });
    }

    const created = await addRecord({ nombre, email });
    res.status(201).json(created);
  } catch (error) {
    res.status(500).json({ message: 'Error al guardar los datos' });
  }
});

export default router;



