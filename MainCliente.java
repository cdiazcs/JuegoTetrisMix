public class MainCliente {

    public static void main(String[] args) {

        String ipServidor =
                "localhost";

        int puerto =
                5000;

        Cliente cliente =
                new Cliente(
                        ipServidor,
                        puerto
                );

        cliente.conectar();
    }
}