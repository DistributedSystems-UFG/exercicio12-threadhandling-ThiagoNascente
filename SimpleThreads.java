public class SimpleThreads {

    // Display a message, preceded by the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };
            try {
                for (String info : importantInfo){
                    // Pause for 4 seconds
                    Thread.sleep(4000);
                    // Print a message
                    threadMessage(info); // gosto mais com iteradores
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    private static class CpuIntensiveTask implements Runnable {
        public void run() {
            long number = 0;
            long primesFound = 0;
            
            // A thread verifica constantemente se foi interrompida
            while (!Thread.currentThread().isInterrupted()) {
                if (isPrime(number)) {
                    primesFound++;
                }
                number++;
                
                // Apenas para dar um sinal de vida a cada 1 milhão de números verificados
                if (number % 1000000 == 0) {
                    threadMessage("Calculando... Já achei " + primesFound + " primos.");
                }
            }
            // Abaixo evidenciamos que a flag de interrupção virou 'true'
            threadMessage("Fui interrompida! Parei o cálculo. Primos totais encontrados: " + primesFound);
        }
        // Método auxiliar para checar se é primo (exige bastante da CPU para números grandes)
        private boolean isPrime(long n) {
            if (n <= 1) return false;
            for (long i = 2; i * i <= n; i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
    }

    public static void main(String args[]) throws InterruptedException {

        // 10 segundos é mais facil para um teste
        long patience = 1000 * 10; 

        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("O argumento deve ser um inteiro.");
                System.exit(1);
            }
        }

        threadMessage("Iniciando as threads MessageLoop e CpuIntensiveTask");
        long startTime = System.currentTimeMillis();
        
        // Dando nomes às threads para ficar claro no console
        Thread t1 = new Thread(new MessageLoop(), "Thread-Mensagens");
        Thread t2 = new Thread(new CpuIntensiveTask(), "Thread-CPU");

        t1.start();
        t2.start();

        threadMessage("Esperando as threads terminarem...");
    
        // O loop agora continua enquanto QUALQUER UMA das threads estiver viva
        while (t1.isAlive() || t2.isAlive()) {
            threadMessage("Ainda esperando...");
            
            // Em vez de usar t.join() para monitorar, usamos o sleep na thread principal
            // para checar o status das duas threads a cada 1 segundo.
            Thread.sleep(1000);
            
            // Verifica se a paciência esgotou e se alguma thread ainda está rodando
            if (((System.currentTimeMillis() - startTime) > patience) && (t1.isAlive() || t2.isAlive())) {
                threadMessage("Cansei de esperar! Interrompendo todos...");
                
                // Interrompe quem ainda estiver vivo
                if (t1.isAlive()) t1.interrupt();
                if (t2.isAlive()) t2.interrupt();
                
                // Espera ambas finalizarem as rotinas de cancelamento
                t1.join();
                t2.join();
            }
        }
        threadMessage("Finalmente todas terminaram!");
    }
}
