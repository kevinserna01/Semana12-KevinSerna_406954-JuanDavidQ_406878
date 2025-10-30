import { readAll, appendOne } from '../utils/fileStore.js';

export async function listRecords() {
  const records = await readAll();
  return records;
}

export async function addRecord(record) {
  const sanitized = {
    nombre: String(record.nombre).trim(),
    email: String(record.email).trim().toLowerCase()
  };
  await appendOne(sanitized);
  return sanitized;
}



