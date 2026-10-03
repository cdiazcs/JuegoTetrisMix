import javax.swing.SwingUtilities;

public class MainClienteGUI {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {

                        VentanaCliente ventana =
                                new VentanaCliente();

                        ventana.setVisible(
                                true
                        );
                    }
                }
        );
    }
}