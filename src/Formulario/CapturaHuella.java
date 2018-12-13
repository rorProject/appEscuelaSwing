/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formulario;
import Formulario.FormularioDatos;
import BDD.ConexionBDD;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Eric
 */

public class CapturaHuella extends javax.swing.JFrame {

    //public String DNI = "3523";

    /**
     * Nuevo Form CapturaHuella
     */
    public CapturaHuella() {
        
        
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "no se puede modificar el tema visual","LookandFeel invalido",
            JOptionPane.ERROR_MESSAGE);
        }
        initComponents();
    }
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    
    protected void iniciar (){
        Lector.addDataListener(new DPFPDataAdapter(){
          @Override public void dataAcquired(final DPFPDataEvent e){
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run(){
                        EnviarTexto("La huella digital ha sido Capturada");
                        ProcesarCaptura(e.getSample());
                        }

                    });
                }
            }
        );
    
    Lector.addReaderStatusListener(new DPFPReaderStatusAdapter(){
          @Override public void readerConnected(final DPFPReaderStatusEvent e){
            SwingUtilities.invokeLater(new Runnable(){
                @Override public void run(){
                    EnviarTexto("El sensor de huella digital esta conectado");
                    }
                });
            }
          @Override public void readerDisconnected(final DPFPReaderStatusEvent e){
            SwingUtilities.invokeLater(new Runnable(){
                @Override public void run(){
                    EnviarTexto("El sensor de huella digital esta desconectado");
                    }
                });
            }
        });
        Lector.addSensorListener(new DPFPSensorAdapter(){
          @Override public void fingerTouched(final DPFPSensorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    @Override public void run(){
                        EnviarTexto("El dedo ha sido colocado sobre el lector de huella");
                    }
                });
                
            }
          @Override public void fingerGone(final DPFPSensorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    @Override public void run(){
                        EnviarTexto("El dedo ha sido quitado del lector de huella");
                    }
                });
            }
        });
          Lector.addErrorListener(new DPFPErrorAdapter(){
                    public void errorReader(final DPFPErrorEvent e){
                        SwingUtilities.invokeLater(new Runnable(){
          @Override public void run(){
                        EnviarTexto("Error: " + e.getError());
                    }
                });
            }
        });
    }
    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;
    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, 
            DPFPDataPurpose purpose){
            DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            
            try {
                return extractor.createFeatureSet(sample, purpose);
            }catch(DPFPImageQualityException e){
                return null;
            }
        }
            public Image CrearImagenHuella(DPFPSample sample){
                return DPFPGlobal.getSampleConversionFactory().createImage(sample);
                }
                    
            public void DibujarHuella(Image image){
                LabelHuella.setIcon(new ImageIcon(image.getScaledInstance(LabelHuella.getWidth(),
                        LabelHuella.getHeight(),
                        image.SCALE_DEFAULT)));
                    repaint();
                    }
                    
            public void EstadoHuellas(){
                EnviarTexto("Muestra de Huellas Necesarias para Guardar exitosamente " + Reclutador.getFeaturesNeeded());
                }
    
            public void start(){
                Lector.startCapture();
                    EnviarTexto("Utilizando el Lector de Huella Dactilar");
                    }
                    
            public void stop(){
                Lector.stopCapture();
                    EnviarTexto("No se está Usando el Lector de Huella Dactilar");
                    }
            
            public void EnviarTexto(String string){
                txtArea.append(string + "\n");
                }
            
            public DPFPTemplate getTemplate(){
                return template;
                }
                    
            public void setTemplate(DPFPTemplate template){
                DPFPTemplate old = this.template;
                this.template = template;
                firePropertyChange(TEMPLATE_PROPERTY, old, template);
                }
   
                    public void ProcesarCaptura(DPFPSample sample){
                    //Procesar la muestra de huella y crear conjunto de caracteristicas para inscripcion
                        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                    
                    //Procesar la muestra de huella y crear conjunto de caracteristicas para verificacion
                        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);  
                    
                    //Comprobar la calidad de la muestra de la huella y lo añade al reclutador si es bueno
                        if(featuresinscripcion !=null){
                            try{
                                System.out.println("Las Caracteristicas de la Huella se han Creado");
                                //Agrega las caracteristicas de la Huella a la Plantilla a Crear
                                Reclutador.addFeatures(featuresinscripcion);
                    
                                //Dibuja la Huella Capturada
                                Image image = CrearImagenHuella(sample);
                                DibujarHuella(image);
                    
                                BtnVerificar.setEnabled(true);
                                BtnEgreso.setEnabled(true);
                                BtnIdentificar.setEnabled(true);
                    
                            }catch(DPFPImageQualityException ex){
                                System.err.println("Error: " + ex.getMessage());
                            }finally{
                                EstadoHuellas();
                                //Comprubea si la plantilla se creo
                        switch(Reclutador.getTemplateStatus()){
                        
                        case TEMPLATE_STATUS_READY: //informe de exito 
                            stop();
                            setTemplate(Reclutador.getTemplate());
                                EnviarTexto("La plantilla de la huella ha sido creada, ya puede verificar e identificarla");
                    
                                BtnIdentificar.setEnabled(false);
                                BtnVerificar.setEnabled(false);
                                BtnEgreso.setEnabled(false);
                                BtnGuardar.setEnabled(true);
                                BtnGuardar.grabFocus();
                                break;
                            
                        case TEMPLATE_STATUS_FAILED: 
                                Reclutador.clear();
                                stop();
                                EstadoHuellas();
                                setTemplate(null);
                                JOptionPane.showMessageDialog(CapturaHuella.this, "La Plantilla de la Huella no puede ser creada. Repita la operacion");
                                start();
                                break;
                    }
                }
                
            }
                       
                    
        }
ConexionBDD conn = new ConexionBDD();
    public void guardarHuella() throws SQLException{
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize());
        Integer tamañoHuella = template.serialize().length;
        
        
        //FormularioDatos var = new FormularioDatos();
        String DNI = JOptionPane.showInputDialog("DNI: ");
        
        
        try{
            Connection c = conn.conectar();
            PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO somhue(hueDNI, huehuella) values(?,?)");
            guardarStmt.setString(1,DNI);
            guardarStmt.setBinaryStream(2, datosHuella,tamañoHuella);
            
            //ejecutar la sentencia
            guardarStmt.execute();
            guardarStmt.close();
            PreparedStatement creartabla = c.prepareStatement("CREATE TABLE Horarios"+ DNI +" (horario_ID Integer, somhue_id varchar(8), horaingreso varchar(50), horaegreso varchar(50), PRIMARY KEY (Horario_ID), FOREIGN KEY (somhue_id) REFERENCES somhue(hueDNI))");
            creartabla.execute();
            creartabla.close();
            JOptionPane.showConfirmDialog(null, "Huella guardada correctamente");
                
                FormularioDatos n = new FormularioDatos();
                    n.setVisible(true);
                    this.setVisible(false);
            
            conn.desconectar();
            BtnGuardar.setEnabled(false);
            BtnVerificar.grabFocus();
        } catch(SQLException ex){
            //indica error en la consola
            System.err.println("Error al guardar los datos de la Huella " + ex);
            
        } finally{
            conn.desconectar();
            
        }
        //INSERT INTO somhue (Hora_ingreso) values(?) WHERE huehuella = huella;
    }
    public void verificarHuella(String DNI){
        try{
            Connection c = conn.conectar();
            
            PreparedStatement verificarStmt = c.prepareStatement("SELECT huehuella FROM somhue WHERE hueDNI=?");
            verificarStmt.setString(1,DNI);
            ResultSet rs = verificarStmt.executeQuery();
            
            if (rs.next()){
            byte templateBuffer[] = rs.getBytes("huehuella");
            DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
            
            setTemplate(referenceTemplate);
            
            DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
            
            if(result.isVerified()){
                JOptionPane.showConfirmDialog(null, "Las huellas capturadas coinciden con la de "+DNI,"Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE);
                        
            }else{
                JOptionPane.showConfirmDialog(null, "No corresponde la huella con "+DNI, "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
            }
        }else{
                JOptionPane.showConfirmDialog(null, "No existe un registro de huella para "+DNI, "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
                }
            
    }catch(SQLException ex){
        System.err.println("Error al verificar los datos de la huella" + ex.getMessage());
        
    }finally{
            conn.desconectar();
            
            
        }
            
            

    }
    
    public void identificarHuella() throws IOException{ //INGRESO!!!!!!!!!!!
        Date horaingreso = new Date();
        
        try{
            
            Connection c = conn.conectar();
            
            PreparedStatement identificarStmt = c.prepareStatement("SELECT hueDNI, huehuella FROM somhue");
            ResultSet rs = identificarStmt.executeQuery();
            
            while(rs.next()){
                byte templateBuffer[] = rs.getBytes("huehuella");
                String DNI = rs.getString("hueDNI");
                
                
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                
                setTemplate(referenceTemplate);
                
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
                
                if(result.isVerified()){
                    JOptionPane.showConfirmDialog(null, "La Huella capturada es de "+DNI,"Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE);
                    //intento de guardar la hora!
                    PreparedStatement hora = c.prepareStatement("INSERT INTO Horarios"+ DNI +" (horaingreso) values ('"+horaingreso+"','"+DNI+"')");
                    hora.execute();
                    hora.close();
                    return;
                }
            }
            JOptionPane.showConfirmDialog(null, "No existe registro que coincida con la huella","Verificacion de huella",JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
            
        }catch (SQLException e){
            System.err.println("Error al identificar huella"+e.getMessage());
        }finally{
            conn.desconectar();
        }
    }
    private void EgresoHuella() throws IOException {
        
        Date horaegreso = new Date();
        
        try{
            
            Connection c = conn.conectar();
            
            PreparedStatement identificarStmt = c.prepareStatement("SELECT hueDNI, huehuella FROM somhue");
            ResultSet rs = identificarStmt.executeQuery();
            
            while(rs.next()){
                byte templateBuffer[] = rs.getBytes("huehuella");
                String DNI = rs.getString("hueDNI");
                
                
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                
                setTemplate(referenceTemplate);
                
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
                
                if(result.isVerified()){
                    JOptionPane.showConfirmDialog(null, "La Huella capturada es de "+DNI,"Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE);
                    //intento de guardar la hora!
                    PreparedStatement hora = c.prepareStatement("INSERT INTO Horarios"+ DNI +" (horaegreso, somhue_id) values ('"+horaegreso+"','"+DNI+"')");
                    hora.execute();
                    hora.close();
                    return;
                }
            }
            JOptionPane.showConfirmDialog(null, "No existe registro que coincida con la huella","Verificacion de huella",JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
            
        }catch (SQLException e){
            System.err.println("Error al identificar huella"+e.getMessage());
        }finally{
            conn.desconectar();
        }
        
    }
    
    /**
     * 
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHuella = new javax.swing.JPanel();
        LabelHuella = new javax.swing.JLabel();
        PanelOpc = new javax.swing.JPanel();
        BtnVerificar = new javax.swing.JButton();
        BtnGuardar = new javax.swing.JButton();
        BtnIdentificar = new javax.swing.JButton();
        BtnSalir = new javax.swing.JButton();
        BtnEgreso = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelHuella.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Huella", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        LabelHuella.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panelHuellaLayout = new javax.swing.GroupLayout(panelHuella);
        panelHuella.setLayout(panelHuellaLayout);
        panelHuellaLayout.setHorizontalGroup(
            panelHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHuellaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116))
        );
        panelHuellaLayout.setVerticalGroup(
            panelHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelHuella, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        );

        PanelOpc.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Opciones", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        BtnVerificar.setText("Verificar");
        BtnVerificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnVerificarActionPerformed(evt);
            }
        });

        BtnGuardar.setText("Guardar");
        BtnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGuardarActionPerformed(evt);
            }
        });

        BtnIdentificar.setText("Ingreso");
        BtnIdentificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnIdentificarActionPerformed(evt);
            }
        });

        BtnSalir.setText("Salir");
        BtnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSalirActionPerformed(evt);
            }
        });

        BtnEgreso.setText("Egreso");
        BtnEgreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEgresoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelOpcLayout = new javax.swing.GroupLayout(PanelOpc);
        PanelOpc.setLayout(PanelOpcLayout);
        PanelOpcLayout.setHorizontalGroup(
            PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOpcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BtnIdentificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnEgreso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(44, 44, 44)
                .addComponent(BtnVerificar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addGroup(PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BtnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .addComponent(BtnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelOpcLayout.setVerticalGroup(
            PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOpcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnGuardar)
                    .addComponent(BtnEgreso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnIdentificar)
                    .addComponent(BtnSalir)
                    .addComponent(BtnVerificar))
                .addContainerGap())
        );

        txtArea.setColumns(20);
        txtArea.setRows(5);
        jScrollPane1.setViewportView(txtArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelOpc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelHuella, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHuella, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(PanelOpc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSalirActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_BtnSalirActionPerformed

    private void BtnVerificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnVerificarActionPerformed
        // TODO add your handling code here:
        String nombre = JOptionPane.showInputDialog("Nombre a verificar: ");
        verificarHuella(nombre);
        Reclutador.clear();
    }//GEN-LAST:event_BtnVerificarActionPerformed

    private void BtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnGuardarActionPerformed
        // TODO add your handling code here:
        try{
            guardarHuella();
            Reclutador.clear();
            LabelHuella.setIcon(null);
            start();
        }catch(SQLException e){
            Logger.getLogger(CapturaHuella.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_BtnGuardarActionPerformed

    private void BtnIdentificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnIdentificarActionPerformed
        // TODO add your handling code here:
        try{
            identificarHuella();
            Reclutador.clear();
        }catch(IOException e){
            Logger.getLogger(CapturaHuella.class.getName()).log(Level.SEVERE, null, e);
            
        }
    }//GEN-LAST:event_BtnIdentificarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        iniciar();
        start();
        EstadoHuellas();
        BtnGuardar.setEnabled(false);
        BtnIdentificar.setEnabled(false);
        BtnVerificar.setEnabled(false);
        BtnEgreso.setEnabled(false);
        BtnSalir.grabFocus();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        stop();
    }//GEN-LAST:event_formWindowClosing

    private void BtnEgresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEgresoActionPerformed
        // TODO add your handling code here:
        try{
            EgresoHuella();
            Reclutador.clear();
        }catch(IOException e){
            Logger.getLogger(CapturaHuella.class.getName()).log(Level.SEVERE, null, e);
            
        }
    }//GEN-LAST:event_BtnEgresoActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }   catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }   catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }   catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }   catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(CapturaHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                new CapturaHuella().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnEgreso;
    private javax.swing.JButton BtnGuardar;
    private javax.swing.JButton BtnIdentificar;
    private javax.swing.JButton BtnSalir;
    private javax.swing.JButton BtnVerificar;
    private javax.swing.JLabel LabelHuella;
    private javax.swing.JPanel PanelOpc;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelHuella;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration//GEN-END:variables

    
}
