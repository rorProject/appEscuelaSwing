/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Formulario;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
/**
 *
 * @author Eric Haas
 */

public class CapturaHuella extends javax.swing.JFrame {


    /**
     * Nuevo Form CapturaHuella
     */
    public CapturaHuella() {
        
        
        
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se puede modificar el tema visual","LookandFeel invalido",
            JOptionPane.ERROR_MESSAGE);
        }
        initComponents();
        this.setLocationRelativeTo(null);
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
                EnviarTexto("Ingrese su huella " + Reclutador.getFeaturesNeeded()+" veces mas");
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
                                EnviarTexto("La plantilla de la huella ha sido creada");
                    
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
                                JOptionPane.showMessageDialog(CapturaHuella.this, "Repita la operacion ha ocurrido un error");
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
        
        
        
        String DNI = JOptionPane.showInputDialog("DNI: ");
        
        
        try{
            Connection c = conn.conectar();
            PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO somhue(hueDNI, huehuella) values(?,?)");
            guardarStmt.setString(1,DNI);
            guardarStmt.setBinaryStream(2, datosHuella,tamañoHuella);
            
            //ejecutar la sentencia
            guardarStmt.execute();
            guardarStmt.close();
            JOptionPane.showMessageDialog(null, "Huella guardada correctamente");
                
            
            FormularioDatos2 n = new FormularioDatos2();
            System.out.println(n.isVisible());
            n.setVisible(true);
            n.setAlwaysOnTop(true);
            System.out.println(n.isVisible());
//            n.grabFocus();
//            n.setEnabled(true);
            
            
            conn.desconectar();
            BtnGuardar.setEnabled(false);
            BtnVerificar.grabFocus();
        } catch(SQLException ex){
            //indica error en la consola
            System.err.println("Error al guardar los datos de la Huella " + ex);
            JOptionPane.showMessageDialog(null, "El DNI ingresado ya esta registrado.");
            
        } finally{
            conn.desconectar();
            
        }
        
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
                    JOptionPane.showMessageDialog(null, "La huella capturada coincide con la de "+DNI,"Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE);
                        
                }else{
                    JOptionPane.showMessageDialog(null, "La huella no corresponde con la de  "+DNI, "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null, "No existe un registro de huella para "+DNI, "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
            }
            
        }catch(SQLException ex){
        System.err.println("Error al verificar los datos de la huella" + ex.getMessage());
        
        }finally{
            conn.desconectar();   
        }
    }
    
    public void ingresoHuella() throws IOException{
        
        //cambiar este boton para automatizar la entrada y la salida!
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String horaingreso = format.format( new Date());
        
        SimpleDateFormat tiempo = new SimpleDateFormat("HH:mm:ss");
        String horario = tiempo.format(new Date());
        
        String observacion = JOptionPane.showInputDialog("Observacion: ");
        if(observacion == null){
            observacion = "";
        }
        try{
            
            Connection c = conn.conectar();
            
            PreparedStatement identificarStmt = c.prepareStatement("SELECT hueDNI, huehuella, nombre FROM somhue");
            ResultSet rs = identificarStmt.executeQuery();
            
            while(rs.next()){
                byte templateBuffer[] = rs.getBytes("huehuella");
                String DNI = rs.getString("hueDNI");
                String nombre = rs.getString("nombre");
                
                
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                
                setTemplate(referenceTemplate);
                
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
                
                if(result.isVerified()){
                    //intento de guardar la hora!
                    if(observacion.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Se agrego el horario de ingreso a: "+DNI,"Horario de ingreso: "+horario, JOptionPane.INFORMATION_MESSAGE);
                        PreparedStatement hora = c.prepareStatement("INSERT INTO horarios (dni, nombre, horaingreso) values ('"+DNI+"','"+nombre+"','"+horaingreso+"')");
                        hora.execute();
                        hora.close();
                    }else{
                        JOptionPane.showMessageDialog(null, "Se agrego el horario de ingreso y la observacion a: "+DNI,"Horario de ingreso: "+horario, JOptionPane.INFORMATION_MESSAGE);
                        PreparedStatement hora = c.prepareStatement("INSERT INTO horarios (dni, nombre, horaingreso, observaciones) values ('"+DNI+"','"+nombre+"','"+horaingreso+"','"+observacion+"')");
                        hora.execute();
                        hora.close();
                    }
                    
                    
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "No existe registro que coincida con la huella","Verificacion de huella",JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
            
        }catch (SQLException e){
            System.err.println("Error al identificar huella"+e.getMessage());
        }finally{
            conn.desconectar();
        }
    }
    private void EgresoHuella() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String horaegreso = format.format( new Date());
        
        SimpleDateFormat tiempo = new SimpleDateFormat("HH:mm:ss");
        String horario = tiempo.format(new Date());
        //agrego la opcion de agregar una observacion desde el usuario
        String observacion = JOptionPane.showInputDialog("Observacion: ");
        if(observacion == null){
            observacion = "";
        }
        
        try{
            
            Connection c = conn.conectar();
            
            PreparedStatement identificarStmt = c.prepareStatement("SELECT hueDNI, huehuella, nombre FROM somhue");
            ResultSet rs = identificarStmt.executeQuery();
            
            while(rs.next()){
                byte templateBuffer[] = rs.getBytes("huehuella");
                String DNI = rs.getString("hueDNI");
                String nombre = rs.getString("nombre");
                
                
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                
                setTemplate(referenceTemplate);
                
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
                
                if(result.isVerified()){
                    if(observacion.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Se agrego el horario de egreso a: "+DNI,"Horario de egreso: "+horario, JOptionPane.INFORMATION_MESSAGE);
                        //intento de guardar la hora!
                        PreparedStatement hora = c.prepareStatement("INSERT INTO horarios (dni, nombre, horaegreso) values ('"+DNI+"','"+nombre+"','"+horaegreso+"')");
                        hora.execute();
                        hora.close();
                    }else{
                        JOptionPane.showMessageDialog(null, "Se agrego el horario de egreso y la observacion a: "+DNI,"Horario de egreso: "+horario, JOptionPane.INFORMATION_MESSAGE);
                        //intento de guardar la hora!
                        PreparedStatement hora = c.prepareStatement("INSERT INTO horarios (dni, nombre, horaegreso, observaciones) values ('"+DNI+"','"+nombre+"','"+horaegreso+"','"+observacion+"')");
                        hora.execute();
                        hora.close();
                    }
                    
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "No existe registro que coincida con la huella","Verificacion de huella",JOptionPane.ERROR_MESSAGE);
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
        BtnVerificar = new javax.swing.JButton();
        BtnSalir = new javax.swing.JButton();
        PanelOpc = new javax.swing.JPanel();
        BtnGuardar = new javax.swing.JButton();
        BtnIdentificar = new javax.swing.JButton();
        BtnEgreso = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(47, 88, 115));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelHuella.setBackground(new java.awt.Color(255, 255, 255));
        panelHuella.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        LabelHuella.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        BtnVerificar.setBackground(new java.awt.Color(255, 255, 255));
        BtnVerificar.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        BtnVerificar.setForeground(new java.awt.Color(0, 187, 255));
        BtnVerificar.setText("?");
        BtnVerificar.setPreferredSize(new java.awt.Dimension(27, 27));
        BtnVerificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnVerificarActionPerformed(evt);
            }
        });

        BtnSalir.setBackground(new java.awt.Color(255, 255, 255));
        BtnSalir.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BtnSalir.setForeground(new java.awt.Color(255, 0, 0));
        BtnSalir.setText("x");
        BtnSalir.setPreferredSize(new java.awt.Dimension(27, 27));
        BtnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelHuellaLayout = new javax.swing.GroupLayout(panelHuella);
        panelHuella.setLayout(panelHuellaLayout);
        panelHuellaLayout.setHorizontalGroup(
            panelHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHuellaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addGroup(panelHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BtnVerificar, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(BtnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelHuellaLayout.setVerticalGroup(
            panelHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHuellaLayout.createSequentialGroup()
                .addComponent(BtnVerificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(BtnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 197, Short.MAX_VALUE))
            .addComponent(LabelHuella, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PanelOpc.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BtnGuardar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BtnGuardar.setText("Guardar");
        BtnGuardar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        BtnGuardar.setPreferredSize(new java.awt.Dimension(71, 71));
        BtnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGuardarActionPerformed(evt);
            }
        });

        BtnIdentificar.setBackground(new java.awt.Color(255, 255, 255));
        BtnIdentificar.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        BtnIdentificar.setForeground(new java.awt.Color(0, 0, 0));
        BtnIdentificar.setText("Ingreso");
        BtnIdentificar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 102)));
        BtnIdentificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnIdentificarActionPerformed(evt);
            }
        });

        BtnEgreso.setBackground(new java.awt.Color(255, 0, 0));
        BtnEgreso.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        BtnEgreso.setForeground(new java.awt.Color(0, 0, 0));
        BtnEgreso.setText("Egreso");
        BtnEgreso.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(0, 0, 0), null, null));
        BtnEgreso.setPreferredSize(new java.awt.Dimension(82, 31));
        BtnEgreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEgresoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jLabel1.setText("DigitalPersona");

        javax.swing.GroupLayout PanelOpcLayout = new javax.swing.GroupLayout(PanelOpc);
        PanelOpc.setLayout(PanelOpcLayout);
        PanelOpcLayout.setHorizontalGroup(
            PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelOpcLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelOpcLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(PanelOpcLayout.createSequentialGroup()
                        .addComponent(BtnIdentificar, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnEgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        PanelOpcLayout.setVerticalGroup(
            PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelOpcLayout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addGroup(PanelOpcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnIdentificar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnEgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 22, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        txtArea.setColumns(20);
        txtArea.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        txtArea.setRows(5);
        jScrollPane1.setViewportView(txtArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelOpc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(panelHuella, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHuella, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelOpc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSalirActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_BtnSalirActionPerformed

    private void BtnVerificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnVerificarActionPerformed
        // TODO add your handling code here:
        String DNI = JOptionPane.showInputDialog("DNI a verificar: ");
        verificarHuella(DNI);
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
            ingresoHuella();
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelHuella;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration//GEN-END:variables

    
}
