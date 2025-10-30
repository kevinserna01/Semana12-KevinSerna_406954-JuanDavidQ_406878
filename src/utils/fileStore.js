import { promises as fs } from 'fs';
import path from 'path';

const dataDir = path.resolve(process.cwd(), 'data');
const dataFile = path.join(dataDir, 'data.txt');

async function ensureStore() {
  try {
    await fs.mkdir(dataDir, { recursive: true });
    await fs.access(dataFile).catch(async () => {
      await fs.writeFile(dataFile, '[]', 'utf-8');
    });
  } catch {
    // If ensure fails, operations will throw later with clearer context
  }
}

export async function readAll() {
  await ensureStore();
  const content = await fs.readFile(dataFile, 'utf-8');
  try {
    const parsed = JSON.parse(content);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    // If file is corrupt, reset to empty array
    await fs.writeFile(dataFile, '[]', 'utf-8');
    return [];
  }
}

export async function appendOne(record) {
  await ensureStore();
  const current = await readAll();
  current.push(record);
  const serialized = JSON.stringify(current, null, 2);
  await fs.writeFile(dataFile, serialized, 'utf-8');
}



