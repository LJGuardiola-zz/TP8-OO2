package guardiola.framework;

import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public final class Application {

    private final ArrayList<Action> actions = new ArrayList<>();;

    public Application() {
        init();
    }

    private void init() {
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            scan(properties.getProperty("fwg.actions"));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de configuración.", e);
        }
    }

    private void scan(String actionsClassPath) {
        try {
            for (Class<? extends Action> actionClass : new Reflections(actionsClassPath).getSubTypesOf(Action.class)) {
                actions.add(
                        actionClass.getDeclaredConstructor().newInstance()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron cargar las Action Class.", e);
        }
    }

    public void start() {

        Scanner scanner = new Scanner(System.in);

        do {

            for (int i = 0; i < actions.size(); i++)
                System.out.println(i + " - " + actions.get(i).toString());

            System.out.print("Elige un opción: ");

            try {
                actions.get(scanner.nextInt()).run();
            } catch (IndexOutOfBoundsException e) {
                System.out.println("La opción ingresada no es válida.");
            }

            System.out.print("Ingrese 1 para continuar o cualquier otro carácter para salir: ");

        } while (scanner.hasNextInt() && scanner.nextInt() == 1);

    }

}
