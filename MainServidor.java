public class MainServidor {

    public static void main(String[] args) {

        int puerto = 5000;

        Servidor servidor =
                new Servidor(puerto);

        servidor.iniciar();
    }
}