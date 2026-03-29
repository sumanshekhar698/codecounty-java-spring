package dev.codecounty.orm.service;

import com.opencsv.CSVReader;

import dev.codecounty.orm.entities.Task;
import dev.codecounty.orm.entities.Worker;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.util.*;

@Service
public class WorkerCsvLoaderServiceSafe {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void loadDataFromCsv() {

        // Prevent duplicate workers
        Map<String, Worker> workerMap = new HashMap<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("data/workers.csv"))))) {

            String[] line;
            reader.readNext(); // Skip header

            while ((line = reader.readNext()) != null) {

                String workerName = line[0];
                String taskName = line[1];

                // 1️ Get or create Worker
                Worker worker = workerMap.get(workerName);
                if (worker == null) {
                    worker = new Worker();
                    worker.setName(workerName);
                    worker.setTasks(new ArrayList<>());
                    workerMap.put(workerName, worker);
                }

                // 2️ Create Task
                Task task = new Task();
                task.setTaskName(taskName);
                task.setWorker(worker); // owning side

                worker.getTasks().add(task); // inverse side
            }

            // 3️ Persist workers (tasks cascade automatically)
            for (Worker worker : workerMap.values()) {
                sessionFactory.getCurrentSession().persist(worker);
            }

            System.out.println("CSV Import Successful. Workers saved: " + workerMap.size());

        } catch (Exception e) {
            throw new RuntimeException("CSV import failed", e);
        }
    }

    @Transactional
    public void loadDataFromCsvIdempotent() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("data/workers.csv"))))) {

            String[] line;
            reader.readNext(); // Skip header

            var session = sessionFactory.getCurrentSession();

            while ((line = reader.readNext()) != null) {
                String workerName = line[0];
                String taskName = line[1];

                // 1. Fetch an existing Worker or create a new one
                Worker worker = session.createQuery("FROM Worker WHERE name = :name", Worker.class)
                        .setParameter("name", workerName)
                        .uniqueResultOptional()
                        .orElseGet(() -> {
                            Worker newWorker = new Worker();
                            newWorker.setName(workerName);
                            newWorker.setTasks(new ArrayList<>());
                            session.persist(newWorker); // Persist immediately to manage it
                            return newWorker;
                        });

                // 2. Check if Task already exists for THIS worker
                boolean taskExists = worker.getTasks().stream()
                        .anyMatch(t -> t.getTaskName().equalsIgnoreCase(taskName));

                if (!taskExists) {
                    Task task = new Task();
                    task.setTaskName(taskName);
                    task.setWorker(worker);
                    
                    worker.getTasks().add(task);
                    session.persist(task); 
                }
            }

            System.out.println("CSV Import Sync Complete.");

        } catch (Exception e) {
            throw new RuntimeException("CSV import failed", e);
        }
    }


    @Transactional
    public void loadDataFromCsvIdempotentBulk() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("data/workers.csv"))))) {

            String[] line;
            reader.readNext(); // Skip header

            var session = sessionFactory.getCurrentSession();

            // 1. Pre-fetch ALL existing workers into a Map for O(1) lookup
            List<Worker> existingWorkers = session.createQuery("SELECT w FROM Worker w LEFT JOIN FETCH w.tasks", Worker.class).getResultList();
            Map<String, Worker> workerMap = new HashMap<>();
            for (Worker w : existingWorkers) {
                workerMap.put(w.getName().toLowerCase(), w);
            }

            while ((line = reader.readNext()) != null) {
                String workerName = line[0];
                String taskName = line[1];
                String workerKey = workerName.toLowerCase();

                // 2. Get from Map (Memory) instead of DB (Disk)
                Worker worker = workerMap.get(workerKey);
                if (worker == null) {
                    worker = new Worker();
                    worker.setName(workerName);
                    worker.setTasks(new ArrayList<>());
                    session.persist(worker);
                    workerMap.put(workerKey, worker); // Add to map so next row finds it
                }

                // 3. Idempotency check for Tasks
                boolean alreadyHasTask = worker.getTasks().stream()
                        .anyMatch(t -> t.getTaskName().equalsIgnoreCase(taskName));

                if (!alreadyHasTask) {
                    Task task = new Task();
                    task.setTaskName(taskName);
                    task.setWorker(worker);
                    worker.getTasks().add(task);
                    session.persist(task);
                }
            }

            System.out.println("Bulk Sync Complete.");

        } catch (Exception e) {
            throw new RuntimeException("Bulk CSV import failed", e);
        }
    }


    @Transactional
    public void loadDataFromCsvIdempotentBulkBatch() {
        int batchSize = 50; // Standard Hibernate batch size
        int count = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("data/workers.csv"))))) {

            String[] line;
            reader.readNext(); // Skip header
            var session = sessionFactory.getCurrentSession();

            while ((line = reader.readNext()) != null) {
                String workerName = line[0];
                String taskName = line[1];

                // 1. Precise Query (Fast because of DB Indexing)
                Worker worker = session.createQuery("FROM Worker WHERE name = :name", Worker.class)
                        .setParameter("name", workerName)
                        .uniqueResultOptional()
                        .orElseGet(() -> {
                            Worker newWorker = new Worker();
                            newWorker.setName(workerName);
                            session.persist(newWorker);
                            return newWorker;
                        });

                // 2. Add Task if not present
                boolean taskExists = session.createQuery(
                                "SELECT 1 FROM Task WHERE taskName = :tName AND worker = :worker", Integer.class)
                        .setParameter("tName", taskName)
                        .setParameter("worker", worker)
                        .uniqueResultOptional().isPresent();

                if (!taskExists) {
                    Task task = new Task();
                    task.setTaskName(taskName);
                    task.setWorker(worker);
                    session.persist(task);
                }

                // 3. The Batch Secret Sauce
                if (++count % batchSize == 0) {
                    session.flush(); // Send SQL to DB
                    session.clear(); // Empty the memory (L1 Cache)
                }
            }

            System.out.println("Row Sync Complete.");

        } catch (Exception e) {
            throw new RuntimeException("Batch CSV import failed", e);
        }
    }
}