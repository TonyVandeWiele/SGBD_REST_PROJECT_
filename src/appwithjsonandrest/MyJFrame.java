package appwithjsonandrest;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.Base64;
import java.util.Iterator;

import org.jfree.chart.plot.XYPlot;
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

    DefaultCategoryDataset ds;

    int[] timestamp;
    double[] accy;
    double[] accz;
    double[] gyroy;
    double[] accx;
    double[] gyroz;
    double[] gyrox;
    String[] classe;
    int nombreLigneJson;


    double[] axesSelected;
    String rowKey;


    JTextField textFieldTimestamp;
    JComboBox<String> ChoixComboBox;

    JComboBox<String> AxesComboBox;


    public SwingWorker<Void, Void> worker;
    /**
     * Type de graphique
     * 0 = Pie Chart
     * 1 = Ligne en 2D
     * 2 = Série temporelle
     * 3 = Autre série temporelle
     * 4 = Bar chart
     * 5 = Scatter plot
     * 6 = Histogram
     */
    public static int graphType = 1;

    public MyJFrame() {
        initComponents();
        showEvolution();
    }
    private void showEvolution()
    {
        //region GUI
        ds = new DefaultCategoryDataset();
        JFreeChart jfc = ChartFactory.createLineChart
                ("Donnees Vehicule", "Temps", "Axes",
                        ds, PlotOrientation.VERTICAL, false, true, false);

        // Obtenez le tracé (plot) du graphique
        CategoryPlot plotCategorie = jfc.getCategoryPlot();

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        // Définir la couleur de la ligne
        renderer.setSeriesPaint(0, java.awt.Color.BLUE);

        plotCategorie.setRenderer(renderer);

        // Obtenez l'axe de l'axe Y (valeur)
        NumberAxis yAxis = (NumberAxis) plotCategorie.getRangeAxis();

        yAxis.setRange(-3,3);

        ChartPanel chartPanel = new ChartPanel(jfc);

        // Créez des boutons
        JButton buttonLecture = new JButton("Lecture");
        JButton buttonStop = new JButton("Stop");
        JButton buttonRetour = new JButton("Retour");
        JButton buttonSnapShot = new JButton("Snapchot");

        // Création d'un JTextField
        textFieldTimestamp = new JTextField(20);
        textFieldTimestamp.addActionListener(a-> {
            int beginTime = Integer.parseInt(textFieldTimestamp.getText()) - 60;
            int endTime = Integer.parseInt(textFieldTimestamp.getText());

            StringBuilder dataJson = GetDataJson(urlGet + "?begintime=" + beginTime + "&endtime=" + endTime);

            parsingJSON(dataJson);
        });

        // Création des choix
        String[] choix = {"TORT","DROIT"};
        // Initialisation de la JComboBox
        ChoixComboBox = new JComboBox<>(choix);

        // Création des axes
        String[] Axes = {"ACCX", "ACCY", "ACCZ"};
        // Initialisation de la JComboBox
        AxesComboBox = new JComboBox<>(Axes);

        // Définissez la taille préférée de la fenêtre
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int preferredWidth = (int) (screenSize.width * 0.8); // Utilisez 80% de la largeur de l'écran
        int preferredHeight = (int) (screenSize.height * 0.8); // Utilisez 80% de la hauteur de l'écran
        Dimension preferredSize = new Dimension(preferredWidth, preferredHeight);
        setPreferredSize(preferredSize);

        // Modifiez le layout du panneau principal pour utiliser BorderLayout.CENTER pour les boutons
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // Créez un panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(buttonLecture);
        buttonPanel.add(buttonStop);
        buttonPanel.add(buttonRetour);
        buttonPanel.add(buttonSnapShot);
        buttonPanel.add(AxesComboBox);
        buttonPanel.add(ChoixComboBox);
        buttonPanel.add(textFieldTimestamp);

        // Ajoutez le panneau des boutons au panneau principal dans la région SUD
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajoutez le panneau principal à la fenêtre
        setContentPane(mainPanel);
        pack();

        // Centrez la fenêtre sur l'écran
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        //endregion

        //region ACTION LISTENER

        AxesComboBox.addActionListener(e -> {

            rowKey = (String) AxesComboBox.getSelectedItem();

            switch (AxesComboBox.getSelectedIndex())
            {
                case 0 : axesSelected = accx;
                         ds.clear();

                        // Définir la couleur de la ligne
                        renderer.setSeriesPaint(0, java.awt.Color.GREEN);
                        plotCategorie.setRenderer(renderer);
                    break;
                case 1 : axesSelected = accy;
                         ds.clear();

                        // Définir la couleur de la ligne
                        renderer.setSeriesPaint(0, Color.BLUE);
                        plotCategorie.setRenderer(renderer);
                    break;
                case 2 :axesSelected = accz;
                        ds.clear();

                        // Définir la couleur de la ligne
                        renderer.setSeriesPaint(0, java.awt.Color.RED);
                        plotCategorie.setRenderer(renderer);
                    break;
                default: axesSelected = accx;
            }

            indexGraphMin = 0;
            indexGraphMax = 20;
        });

        buttonLecture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    ds.clear();

                    for (int i = indexGraphMin; i < nombreLigneJson && i < indexGraphMax; i++) {
                        ds.addValue(axesSelected[i], rowKey, convertTimestamp(timestamp[i]));
                        System.out.println(axesSelected[i]);
                    }

                    worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() {
                            for (int i = indexGraphMax; indexGraphMax < nombreLigneJson; i++) {
                                if (this.isCancelled()) {
                                    System.out.println("CANCELLED");
                                    break; // Si l'annulation est demandée, quittez la boucle
                                }
                                ds.removeValue(rowKey, convertTimestamp(timestamp[indexGraphMin]));
                                ds.addValue(axesSelected[indexGraphMax], rowKey, convertTimestamp(timestamp[indexGraphMax]));
                                indexGraphMin++;
                                indexGraphMax++;
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            chartPanel.repaint();
                        }
                    };
                    worker.execute();
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

            for (int i = indexGraphMin; i >= 0 && i < indexGraphMax; i++)
            {
                ds.setValue(axesSelected[i], rowKey, convertTimestamp(timestamp[i]));
                chartPanel.repaint();
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

                // Récupération de l'heure actuelle
                LocalTime currentTime = LocalTime.now();

                // Formatage de l'heure en chaîne de caractères
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String currentTimeString = currentTime.format(formatter);

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
        //endregion
    }

    public void parsingJSON(StringBuilder dataJson) {
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
                if (key.equals("TIMES_TAMP")) {
                    timestamp[i] = (int) currentObject.get(key);
                    System.out.println(timestamp[i]);
                }
                if (key.equals("ACCY"))
                    accy[i] = (double) currentObject.get(key);
                if (key.equals("ACCZ"))
                    accz[i] = (double) currentObject.get(key);
                if (key.equals("GYROY"))
                    try {
                        gyroy[i] = (double) currentObject.get(key);
                    } catch (ClassCastException e) {
                        System.out.println("Erreur de casting pour gyroy : " + e.getMessage());
                        gyroy[i] = (double) (int) currentObject.get(key);
                    }
                if (key.equals("ACCX"))
                    accx[i] = (double) currentObject.get(key);
                if (key.equals("GYROZ"))
                    try {
                        gyroz[i] = (double) currentObject.get(key);
                    } catch (ClassCastException e) {
                        System.out.println("Erreur de casting pour gyroz : " + e.getMessage());
                        gyroz[i] = (double) (int) currentObject.get(key);
                    }
                if (key.equals("GYROX"))
                    try {
                        gyrox[i] = (double) currentObject.get(key);
                    } catch (ClassCastException e) {
                        System.out.println("Erreur de casting pour gyrox : " + e.getMessage());
                        gyrox[i] = (double) (int) currentObject.get(key);
                    }
                if (key.equals("CLASSE"))
                    classe[i] = (String) currentObject.get(key);
            }
        }
        axesSelected = accx;
        rowKey = (String) AxesComboBox.getSelectedItem();
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

    private static long convertDateFormatToTimestamp(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = sdf.parse(timeString);
            return (date.getTime() / 1000); // Convertir en secondes
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Gérer l'erreur de parsing
        }
    }

/*    private void showHistogram()
    {
        double[] values = { 95, 49, 14, 59, 50, 66, 47, 40, 1, 67,
                12, 58, 28, 63, 14, 9, 31, 17, 94, 71,
                49, 64, 73, 97, 15, 63, 10, 12, 31, 62,
                93, 49, 74, 90, 59, 14, 15, 88, 26, 57,
                77, 44, 58, 91, 10, 67, 57, 19, 88, 84
        };


        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("key", values, 10);

        JFreeChart jfc = ChartFactory.createHistogram("JFreeChart Histogram",
                "Data", "Frequency", dataset);

        ChartPanel cp = new ChartPanel(jfc);
        setContentPane(cp);
    }

    private void showBarChart()
    {
        String jour1 = "lundi", jour2 = "vendredi";
        String atelier1 = "Huy", atelier2 = "Verviers", atelier3 = "Liège", atelier4 = "Waremme";

        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        ds.addValue(25.3, jour1, atelier1);
        ds.addValue(20.7, jour2, atelier1);
        ds.addValue(30.1, jour1, atelier2);
        ds.addValue(34.2, jour2, atelier2);
        ds.addValue(85.3, jour1, atelier3);
        ds.addValue(82.1, jour2, atelier3);
        ds.addValue(19.6, jour1, atelier4);
        ds.addValue(15.9, jour2, atelier4);

        JFreeChart jfc = ChartFactory.createBarChart(
                "Productions de bouteilles de lait (en milliers)",
                "Ateliers",
                "Production",
                ds,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel cp = new ChartPanel(jfc);
        setContentPane(cp);
    }

    private void showScatterPlot()
    {
        double couples[][] = { {3.8,68.0}, {3.6,72.0}, {3.4,86.0}, {3.5,78.0}, {3.9,64.0},
                {4.2,61.0}, {3.7,66.0}, {3.5,74.0}, {3.8,59.0}, {4.1,55.0},
                {3.7,64.0}, {3.6,73.0}, {3.8,62.0}, {3.4,75.0}, {3.5,78.0} };
        XYSeries serieObs = new XYSeries("Relations vision-manipulation");
        for (int i=0; i<15; i++)
            serieObs.add(couples[i][0],couples[i][1]);
        XYSeriesCollection ds = new XYSeriesCollection();
        ds.addSeries(serieObs);

        JFreeChart jfc = ChartFactory.createScatterPlot(
                "Perception visuelle et habileté manuelle",
                "réponse à stimulus visuel", "dextérité manuelle",
                ds,
                PlotOrientation.VERTICAL,
                true, true, false );

        ChartPanel cp = new ChartPanel(jfc);
        setContentPane(cp);
    }

    private void showPieChart()
    {
        // 1. Définir un dataset qui contient les data
        DefaultPieDataset ds = new DefaultPieDataset();
        ds.setValue("Parti du progrès contrôlé", 22.36);
        ds.setValue("Parti démocrate conservateur", 27.69);
        ds.setValue("Intérêts des gens riches", 3.78);
        ds.setValue("Prolétariat uni", 7.85);
        ds.setValue("Mouvement des Gens Heureux", 35.12);
        ds.setValue("Autres", 3.2);
        // 2. Se fournir un JFreeChart
        JFreeChart jfc = ChartFactory.createPieChart (
                "Résulats des élections en Boursoulavie", ds, true, true, true);
        // 3. Fabriquer le Panel
        ChartPanel cp = new ChartPanel(jfc);
        setContentPane(cp);

        SwingUtilities.invokeLater(new Runnable() {public void run() {
            try {
                sleep(5000);}catch(Exception e){}
            ds.setValue("Oubliés", 1.2);}}
        );
    }

    private void showTimeSeries()
    {
        double [][] ProductionsLait = {
                {65.23, 54.54, 59.71, 45.12,
                        32.14, 32.19, 40.84, 46.21,
                        47.67, 42.36, 45.65, 55.81 },
                {60.31, 57.54, 62.71, 49.12,
                        30.14, 38.19, 44.84, 49.21,
                        53.67, 54.36, 49.65, 56.81 }
        };

        String[] lieuxProductions = { "Trois-Ponts", "Stavelot"};
        int annee = 2006;

        Productions[] p = new Productions[2];

        try
        {
            for (int j=0; j<lieuxProductions.length; j++)
                p[j] = new Productions(Calendar.MONTH, ProductionsLait[j], annee);
        }
        catch (InvalidDataProduction e)
        {
            System.out.println("Oh oh ... " + e.getMessage());
        }

        Calendar c = Calendar.getInstance();

        TimeSeriesCollection ds = new TimeSeriesCollection();
        TimeSeries[] s = new TimeSeries[2];
        for (int j=0; j<lieuxProductions.length; j++)
        {
            s[j] = new TimeSeries("Productions de lait en " + p[j].getPeriodeSuperieure() + " à "  + lieuxProductions[j]); //, Month.class);

            for (int i = 1; i<= c.getActualMaximum(p[j].getTypePeriode()); i++ )
            {
                s[j].add(new Month(i, p[j].getPeriodeSuperieure()), p[j].getData()[i-1]);
            }
            ds.addSeries(s[j]);
        }

        JFreeChart jfc = ChartFactory.createTimeSeriesChart(
                "Productions de lait",
                "Date", // x
                "Milliers de litres", // y
                ds,
                true, true, false
        );
        ChartPanel cp = new ChartPanel(jfc);
        setContentPane(cp);
    }

    private XYDataset createDataset()
    {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        TimeSeries series1 = new TimeSeries("Series1");
        series1.add(new Day(1, 1, 2017), 50);
        series1.add(new Day(2, 1, 2017), 40);
        series1.add(new Day(3, 1, 2017), 45);
        series1.add(new Day(4, 1, 2017), 30);
        series1.add(new Day(5, 1, 2017), 50);
        series1.add(new Day(6, 1, 2017), 45);
        series1.add(new Day(7, 1, 2017), 60);
        series1.add(new Day(8, 1, 2017), 45);
        series1.add(new Day(9, 1, 2017), 55);
        series1.add(new Day(10, 1, 2017), 48);
        series1.add(new Day(11, 1, 2017), 60);
        series1.add(new Day(12, 1, 2017), 45);
        series1.add(new Day(13, 1, 2017), 65);
        series1.add(new Day(14, 1, 2017), 45);
        series1.add(new Day(15, 1, 2017), 55);
        dataset.addSeries(series1);

        TimeSeries series2 = new TimeSeries("Series2");
        series2.add(new Day(1, 1, 2017), 40);
        series2.add(new Day(2, 1, 2017), 35);
        series2.add(new Day(3, 1, 2017), 26);
        series2.add(new Day(4, 1, 2017), 45);
        series2.add(new Day(5, 1, 2017), 40);
        series2.add(new Day(6, 1, 2017), 35);
        series2.add(new Day(7, 1, 2017), 45);
        series2.add(new Day(8, 1, 2017), 48);
        series2.add(new Day(9, 1, 2017), 31);
        series2.add(new Day(10, 1, 2017), 32);
        series2.add(new Day(11, 1, 2017), 21);
        series2.add(new Day(12, 1, 2017), 35);
        series2.add(new Day(13, 1, 2017), 10);
        series2.add(new Day(14, 1, 2017), 25);
        series2.add(new Day(15, 1, 2017), 15);
        dataset.addSeries(series2);


        return dataset;
    }*/

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

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
