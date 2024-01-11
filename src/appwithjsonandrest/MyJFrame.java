package appwithjsonandrest;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import java.util.Base64;
import java.util.Iterator;

import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static appwithjsonandrest.GetPost.GetDataJson;
import static appwithjsonandrest.GetPost.POSTImageJson;

public class MyJFrame extends javax.swing.JFrame {

    String urlGet = "http://192.168.175.144:8080/ords/devus/test/a";
    String urlPost = "http://192.168.175.144:8080/ords/devus/test/a";
    int indexGraphMax = 20;
    int indexGraphMin = 0;
    boolean active = false;
    double lowerZoom = -3;
    double upperZoom = 3;

    DefaultCategoryDataset ds;
    LineAndShapeRenderer renderer;
    CategoryPlot plotCategorie;

    int[] timestamp;
    double[] accy;
    double[] accz;
    double[] gyroy;
    double[] accx;
    double[] gyroz;
    double[] gyrox;
    String[] classe;

    int nombreLigneJson;


    double[] dataSelected;

    ChartPanel chartPanel;

    JTextField textFieldTimestamp;
    JComboBox<String> ChoixComboBox;
    JComboBox<String> AxesComboBox;

    JButton buttonLecture;
    JButton buttonStop;
    JButton buttonRetour;
    JButton buttonSnapShot;
    JButton buttonZoom;
    JButton buttonDezoom;

    Map<String, double[]> dataVectors;
    List<JCheckBox> checkBoxes;
    JCheckBox accxCheckBox;
    JCheckBox accyCheckBox;
    JCheckBox acczCheckBox;
    JCheckBox gyroxCheckBox;
    JCheckBox gyroyCheckBox;
    JCheckBox gyrozCheckBox;


    public SwingWorker<Void, Void> worker;

    public MyJFrame() {
        initComponents();
        showEvolution();
    }

    private void showEvolution()
    {
        JPanel mainPanel = new JPanel(new BorderLayout());

        //region CREATION DE LA FENETRE
        ds = new DefaultCategoryDataset();

        dataSelected = accx;

        JFreeChart jfc = ChartFactory.createLineChart
                ("Donnees Vehicule", "Temps", "Axes",
                        ds, PlotOrientation.VERTICAL, true, true, false);

        // Obtenez le tracé (plot) du graphique
        plotCategorie = jfc.getCategoryPlot();

        renderer = new LineAndShapeRenderer();

        // Définir la couleur de la ligne
        renderer.setSeriesPaint(0, java.awt.Color.BLUE);
        renderer.setSeriesPaint(1, java.awt.Color.RED);
        renderer.setSeriesPaint(2, java.awt.Color.GREEN);
        renderer.setSeriesPaint(3, Color.YELLOW);
        renderer.setSeriesPaint(4, Color.ORANGE);
        renderer.setSeriesPaint(5, Color.BLACK);

        plotCategorie.setRenderer(renderer);

        // Obtenez l'axe de l'axe Y (valeur)
        NumberAxis yAxis = (NumberAxis) plotCategorie.getRangeAxis();

        yAxis.setRange(lowerZoom,upperZoom);

        chartPanel = new ChartPanel(jfc);

        // Créez des boutons
        buttonLecture = new JButton("Lecture");
        buttonStop = new JButton("Stop");
        buttonRetour = new JButton("Retour");
        buttonSnapShot = new JButton("Snapshot");
        buttonZoom = new JButton("+");
        buttonDezoom = new JButton("-");


        // Création des checkBoxes pour les axes
        accxCheckBox = new JCheckBox("ACCX");
        accyCheckBox = new JCheckBox("ACCY");
        acczCheckBox = new JCheckBox("ACCZ");
        gyroxCheckBox = new JCheckBox("GYROX");
        gyroyCheckBox = new JCheckBox("GYROY");
        gyrozCheckBox = new JCheckBox("GYROZ");

        // Initialisation de la liste des checkBoxes
        checkBoxes = new ArrayList<>();
        // Ajout des checkBoxes à la liste
        checkBoxes.add(accxCheckBox);
        checkBoxes.add(accyCheckBox);
        checkBoxes.add(acczCheckBox);
        checkBoxes.add(gyroxCheckBox);
        checkBoxes.add(gyroyCheckBox);
        checkBoxes.add(gyrozCheckBox);

        // Création des choix
        String[] choix = {"TORT","DROIT"};

        // Initialisation de la JComboBox
        ChoixComboBox = new JComboBox<>(choix);

        // Création d'un JTextField
        textFieldTimestamp = new JTextField(8);
        
        // Définissez la taille préférée de la fenêtre
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int preferredWidth = (int) (screenSize.width * 0.9); // Utilisez 80% de la largeur de l'écran
        int preferredHeight = (int) (screenSize.height * 0.8); // Utilisez 80% de la hauteur de l'écran
        Dimension preferredSize = new Dimension(preferredWidth, preferredHeight);
        setPreferredSize(preferredSize);

        chartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));  // Marge de 20 pixels autour du ChartPanel
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // Créez un panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));


        buttonPanel.add(new JLabel("Timestamp:"));
        buttonPanel.add(textFieldTimestamp);
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(buttonLecture);
        buttonPanel.add(buttonStop);
        buttonPanel.add(buttonRetour);
        buttonPanel.add(buttonSnapShot);
        buttonPanel.add(buttonZoom);
        buttonPanel.add(buttonDezoom);
        buttonPanel.add(ChoixComboBox);


        // Ajout des cases à cocher au panneau des boutons
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        checkBoxPanel.add(Box.createVerticalStrut(100));
        checkBoxPanel.add(new JLabel("Axes:"));
        checkBoxPanel.add(Box.createVerticalStrut(20));
        checkBoxPanel.add(accxCheckBox);
        checkBoxPanel.add(Box.createVerticalStrut(20));
        checkBoxPanel.add(accyCheckBox);
        checkBoxPanel.add(Box.createVerticalStrut(20));
        checkBoxPanel.add(acczCheckBox);
        checkBoxPanel.add(Box.createVerticalStrut(20));
        checkBoxPanel.add(gyroxCheckBox);
        checkBoxPanel.add(Box.createVerticalStrut(20));
        checkBoxPanel.add(gyroyCheckBox);
        checkBoxPanel.add(Box.createVerticalStrut(20));
        checkBoxPanel.add(gyrozCheckBox);

        // Ajoutez le panneau des boutons au panneau principal dans la région SUD
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(checkBoxPanel, BorderLayout.EAST);

        // Ajoutez le panneau principal à la fenêtre
        setContentPane(mainPanel);
        pack();

        // Centrez la fenêtre sur l'écran
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        desactiveButton();
        //endregion

        //region ACTION LISTENER

        // Utilisation de la méthode pour ajouter des listeners aux cases à cocher
        addCheckBoxListener(accxCheckBox);
        addCheckBoxListener(accyCheckBox);
        addCheckBoxListener(acczCheckBox);
        addCheckBoxListener(gyroxCheckBox);
        addCheckBoxListener(gyroyCheckBox);
        addCheckBoxListener(gyrozCheckBox);
        
        textFieldTimestamp.addActionListener(a-> {
            try {
                int beginTime = Integer.parseInt(textFieldTimestamp.getText()) - 60;
                int endTime = Integer.parseInt(textFieldTimestamp.getText());

                StringBuilder dataJson = GetDataJson(urlGet + "?begintime=" + beginTime + "&endtime=" + endTime);

                parsingJSON(dataJson);
                JOptionPane.showMessageDialog(getContentPane(), "Données chargées avec succès!");
                activeButton();
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(getContentPane(), "Veuillez entrer un timestamp valide!");
            }
                catch (JSONException ex) {
                JOptionPane.showMessageDialog(getContentPane(), "Erreur du JSON! " + ex.getMessage());
            }
                catch (Exception ex) {
                JOptionPane.showMessageDialog(getContentPane(), "Erreur inconnue! " + ex.getMessage());
            }
        });

        buttonLecture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(active)
                {
                    JOptionPane.showMessageDialog(getContentPane(), "Lecture déjà en cours!");
                    return;
                }
                worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        active = true;

                        for (int i = indexGraphMax; indexGraphMax < nombreLigneJson; i++) {
                            int nbrCheckBoxSelected = 0;

                            if (this.isCancelled()) {
                                System.out.println("CANCELLED");
                                break;
                            }

                            if(timestamp[indexGraphMax-1] != timestamp[indexGraphMax])
                            {
                                // Parcourez toutes les cases à cocher et mettez à jour le graphique en conséquence
                                for (JCheckBox checkBox : checkBoxes) {
                                    if (checkBox.isSelected()) {
                                        String key = checkBox.getText(); // Utilise le texte de la case comme clé pour accéder au champ de la HashMap

                                        ds.removeValue(key, convertTimestamp(timestamp[indexGraphMin]));
                                        ds.addValue(dataVectors.get(key)[indexGraphMax], key, convertTimestamp(timestamp[indexGraphMax]));
                                        nbrCheckBoxSelected++;
                                    }
                                }
                                if(nbrCheckBoxSelected == 0)
                                {
                                    JOptionPane.showMessageDialog(getContentPane(), "Veuillez sélectionner au moins une case à cocher!");
                                    break;
                                }

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            indexGraphMin++;
                            indexGraphMax++;
                        }
                        return null;
                    }
                    @Override
                    protected void done() {
                        active = false;
                        chartPanel.repaint();
                    }
                };
                worker.execute();
            }
        });

        buttonStop.addActionListener(e -> {
            if (worker != null && !worker.isDone()) {
                worker.cancel(true); // Demander l'annulation du SwingWorker
                JOptionPane.showMessageDialog(getContentPane(), "Arret de l'opération!");
            }
        });

        buttonRetour.addActionListener(e -> {
            if (worker != null && !worker.isDone()) {
                worker.cancel(true); // Demander l'annulation du SwingWorker
                JOptionPane.showMessageDialog(getContentPane(), "Arret de l'opération!");
            }

            if(indexGraphMin < 20)
            {
                indexGraphMin = 0;
                indexGraphMax = 20;
            }
            else
            {
                indexGraphMin = indexGraphMin - 20;
                indexGraphMax = indexGraphMax - 20;
            }

            ds.clear();

            // Parcourez toutes les cases à cocher et mettez à jour le graphique en conséquence
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String key = checkBox.getText(); // Utilise le texte de la case comme clé pour accéder au champ de la HashMap

                    for (int i = indexGraphMin; i < nombreLigneJson && i < indexGraphMax; i++) {
                        ds.addValue(dataVectors.get(key)[i], key, convertTimestamp(timestamp[i]));
                    }
                }
            }
        });

        buttonSnapShot.addActionListener(e -> {
            String imagePath="chart.png";

            // Créez une image à partir du graphique
            BufferedImage image = jfc.createBufferedImage(800, 600);
            try {
                File imageFile = new File(imagePath);
                ImageIO.write(image, "png", imageFile);
                System.out.println("Image du graphique enregistrée sous chart.png.");

                //Conversion en BASE64 pour envoie
                String base64Image = convertImageToBase64(imagePath);

                // Récupération de l'heure et la date actuelle
                LocalDateTime now = LocalDateTime.now();

                // Formatage de l'heure en chaîne de caractères
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String currentTimeString = now.format(formatter);

                if(POSTImageJson(base64Image,(String) ChoixComboBox.getSelectedItem(), String.valueOf(timestamp[indexGraphMin]), currentTimeString , urlPost) != -1)
                {
                    JOptionPane.showMessageDialog(getContentPane(), "Envoi de l'image réussi!");
                }
                else
                {
                    JOptionPane.showMessageDialog(getContentPane(), "Erreur lors de l'envoi de l'image!");
                }

            } catch (IOException eIO) {
                eIO.printStackTrace();
            }
        });

        buttonZoom.addActionListener( e -> {
            if(upperZoom <= 1)
            {
                if(upperZoom <= 0.2)
                {
                    JOptionPane.showMessageDialog(getContentPane(), "Zoom minimal atteint!");
                    return;
                }
                lowerZoom = lowerZoom + 0.1;
                upperZoom = upperZoom - 0.1;
                yAxis.setRange(lowerZoom,upperZoom);
                return;
            }
            yAxis.setRange(++lowerZoom,--upperZoom);
        });

        buttonDezoom.addActionListener( e -> {
            if(upperZoom >= 10)
            {
                JOptionPane.showMessageDialog(getContentPane(), "Dezoom maximal atteint!");
                return;
            }
            if (upperZoom < 1)
            {
                lowerZoom = lowerZoom - 0.1;
                upperZoom = upperZoom + 0.1;
                yAxis.setRange(lowerZoom,upperZoom);
                return;
            }
            yAxis.setRange(--lowerZoom,++upperZoom);
            chartPanel.repaint();
        });
        //endregion
    }


    public void parsingJSON(StringBuilder dataJson) throws JSONException {
        if (dataJson.length() <= 2) {
            System.out.println("JSON vide.");
            throw new JSONException("Pas de donnée trouvée.");
        }

        // Parsing dans le JSON reçu
        JSONArray received = new JSONArray(new String(dataJson));

        nombreLigneJson = received.length();

        timestamp = new int[nombreLigneJson];
        accy = new double[nombreLigneJson];
        accz = new double[nombreLigneJson];
        gyroy = new double[nombreLigneJson];
        accx = new double[nombreLigneJson];
        gyroz = new double[nombreLigneJson];
        gyrox = new double[nombreLigneJson];
        classe = new String[nombreLigneJson];

        for (int i = 0; i < nombreLigneJson; i++)
        {
            JSONObject currentObject = received.getJSONObject(i);
            Iterator it = currentObject.keys();

            while (it.hasNext()) {
                String key = (String) it.next();
                switch (key) {
                    case "TIMES_TAMP":
                        timestamp[i] = (int) currentObject.get(key);
                        System.out.println(timestamp[i]);
                        break;

                    case "ACCY":
                        try {
                            accy[i] = (double) currentObject.get(key);
                        } catch (ClassCastException e) {
                            System.out.println("Erreur de casting pour " + key + " : " + e.getMessage());
                            accy[i] = (double) (int) currentObject.get(key);
                        }
                        break;

                    case "ACCZ":
                        try {
                            accz[i] = (double) currentObject.get(key);
                        } catch (ClassCastException e) {
                            System.out.println("Erreur de casting pour " + key + " : " + e.getMessage());
                            accz[i] = (double) (int) currentObject.get(key);
                        }
                        break;

                    case "GYROY":
                        try {
                            gyroy[i] = (double) currentObject.get(key);
                        } catch (ClassCastException e) {
                            System.out.println("Erreur de casting pour " + key + " : " + e.getMessage());
                            gyroy[i] = (double) (int) currentObject.get(key);
                        }
                        break;

                    case "GYROZ":
                        try {
                            gyroz[i] = (double) currentObject.get(key);
                        } catch (ClassCastException e) {
                            System.out.println("Erreur de casting pour " + key + " : " + e.getMessage());
                            gyroz[i] = (double) (int) currentObject.get(key);
                        }
                        break;

                    case "GYROX":
                        try {
                            gyrox[i] = (double) currentObject.get(key);
                        } catch (ClassCastException e) {
                            System.out.println("Erreur de casting pour " + key + " : " + e.getMessage());
                            gyrox[i] = (double) (int) currentObject.get(key);
                        }
                        break;

                    case "ACCX":
                        try {
                            accx[i] = (double) currentObject.get(key);
                        } catch (ClassCastException e) {
                            System.out.println("Erreur de casting pour " + key + " : " + e.getMessage());
                            accx[i] = (double) (int) currentObject.get(key);
                        }
                        break;

                    case "CLASSE":
                        classe[i] = (String) currentObject.get(key);
                        break;

                    default:
                        // Gérer le cas par défaut si nécessaire
                        System.out.println("Clé non gérée : " + key);
                }
            }
        }
        // Déclarez la structure de données pour stocker les vecteurs de données avec leurs clés associées
        dataVectors = new HashMap<>();

        // Initialisez les vecteurs de données
        dataVectors.put("ACCX", accx);
        dataVectors.put("ACCY", accy);
        dataVectors.put("ACCZ", accz);
        dataVectors.put("GYROX", gyrox);
        dataVectors.put("GYROY", gyroy);
        dataVectors.put("GYROZ", gyroz);
    }

    private void addCheckBoxListener(JCheckBox checkBox) {
        checkBox.addActionListener(e -> {
            String key = checkBox.getText();
            if (checkBox.isSelected()) {
                // Initialisation des données
                for (int i = indexGraphMin; i < nombreLigneJson && i < indexGraphMax; i++) {
                    ds.addValue(dataVectors.get(key)[i], key, convertTimestamp(timestamp[i]));
                }
                chartPanel.repaint();
            }
            else {
                //Suppression des données
                for (int i = indexGraphMin; i < nombreLigneJson && i < indexGraphMax; i++) {
                    ds.removeValue(key, convertTimestamp(timestamp[i]));
                }
                chartPanel.repaint();
            }
        });
    }

    private void activeButton()
    {
        buttonZoom.setEnabled(true);
        buttonDezoom.setEnabled(true);
        buttonLecture.setEnabled(true);
        buttonRetour.setEnabled(true);
        buttonSnapShot.setEnabled(true);
        buttonStop.setEnabled(true);
        chartPanel.setEnabled(true);
        ChoixComboBox.setEnabled(true);
        accxCheckBox.setEnabled(true);
        accyCheckBox.setEnabled(true);
        acczCheckBox.setEnabled(true);
        gyroxCheckBox.setEnabled(true);
        gyroyCheckBox.setEnabled(true);
        gyrozCheckBox.setEnabled(true);
    }

    private void desactiveButton()
    {
        buttonZoom.setEnabled(false);
        buttonDezoom.setEnabled(false);
        buttonLecture.setEnabled(false);
        buttonRetour.setEnabled(false);
        buttonSnapShot.setEnabled(false);
        buttonStop.setEnabled(false);
        chartPanel.setEnabled(false);
        ChoixComboBox.setEnabled(false);
        accxCheckBox.setEnabled(false);
        accyCheckBox.setEnabled(false);
        acczCheckBox.setEnabled(false);
        gyroxCheckBox.setEnabled(false);
        gyroyCheckBox.setEnabled(false);
        gyrozCheckBox.setEnabled(false);
    }

    public static String convertImageToBase64(String imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(new File(imagePath).toPath());
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private static String convertTimestamp(int timestamp) {
        long millis = timestamp * 1000L;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        return sdf.format(date);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        try{
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        }
        catch (Exception e){
            System.out.println("Erreur lors du chargement du Look and Feel");
        }

        pack();
    }// </editor-fold>//GEN-END:initComponents

}
