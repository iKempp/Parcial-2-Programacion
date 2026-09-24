package co.edu.uniquindio.devplus.gui;

import co.edu.uniquindio.devplus.exception.*;
import co.edu.uniquindio.devplus.model.*;
import co.edu.uniquindio.devplus.service.DevPlusService;
import co.edu.uniquindio.devplus.util.DatosPruebaUtil;
import co.edu.uniquindio.devplus.util.ValidadorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Interfaz Grafica de Usuario (GUI) moderna construida en Java Swing para el sistema DevPlus.
 * Permite ejecutar de forma visual e interactiva todas las funcionalidades y reglas de negocio
 * (RN1 a RN8), interactuando directamente con DevPlusService.
 */
public class VentanaPrincipal extends JFrame {

    private final DevPlusService service;

    // Componentes para actualizacion dinamica
    private JLabel lblTotalProyectos;
    private JLabel lblTotalClientes;
    private JLabel lblTotalDevs;
    private JLabel lblTotalFacturado;

    // Modelos de tablas
    private DefaultTableModel modeloProyectos;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloDevs;
    private DefaultTableModel modeloServicios;

    private JTable tablaProyectos;
    private JTable tablaClientes;
    private JTable tablaDevs;
    private JTable tablaServicios;

    public VentanaPrincipal(DevPlusService service) {
        this.service = service;
        configurarVentana();
        inicializarComponentes();
        refrescarTodasLasTablas();
    }

    private void configurarVentana() {
        setTitle("DevPlus Software - Sistema de Gestion de Proyectos (Programacion I)");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // 1. Encabezado superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(24, 38, 59));
        panelHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("DEVPLUS SOFTWARE - SISTEMA DE GESTION");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Arquitectura en Capas (POO) • Universidad del Quindio • Parcial I 2026");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(180, 195, 215));

        JPanel panelTextoHeader = new JPanel(new GridLayout(2, 1, 0, 3));
        panelTextoHeader.setOpaque(false);
        panelTextoHeader.add(lblTitulo);
        panelTextoHeader.add(lblSubtitulo);

        JButton btnRefrescarGlobal = new JButton("Actualizar Datos");
        btnRefrescarGlobal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescarGlobal.setBackground(new Color(41, 128, 185));
        btnRefrescarGlobal.setForeground(Color.WHITE);
        btnRefrescarGlobal.setFocusPainted(false);
        btnRefrescarGlobal.addActionListener(e -> {
            refrescarTodasLasTablas();
            JOptionPane.showMessageDialog(this, "Datos sincronizados correctamente con el servicio.", "Actualizado", JOptionPane.INFORMATION_MESSAGE);
        });

        panelHeader.add(panelTextoHeader, BorderLayout.WEST);
        panelHeader.add(btnRefrescarGlobal, BorderLayout.EAST);
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // 2. Pestanias principales
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(new Font("Segoe UI", Font.BOLD, 13));

        pestañas.addTab("Dashboard", crearPanelDashboard());
        pestañas.addTab("Proyectos", crearPanelProyectos());
        pestañas.addTab("Clientes", crearPanelClientes());
        pestañas.addTab("Desarrolladores", crearPanelDesarrolladores());
        pestañas.addTab("Servicios Adicionales", crearPanelServicios());
        pestañas.addTab("Funcionalidades Especiales (RN7 / RN8)", crearPanelEspeciales());

        panelPrincipal.add(pestañas, BorderLayout.CENTER);
        setContentPane(panelPrincipal);
    }

    // =========================================================================
    // 1. PANEL DASHBOARD
    // =========================================================================

    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));

        // Metricas en tarjetas
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 4, 15, 0));
        panelTarjetas.setOpaque(false);

        lblTotalProyectos = new JLabel("0", SwingConstants.CENTER);
        lblTotalClientes = new JLabel("0", SwingConstants.CENTER);
        lblTotalDevs = new JLabel("0", SwingConstants.CENTER);
        lblTotalFacturado = new JLabel("$0", SwingConstants.CENTER);

        panelTarjetas.add(crearTarjetaMetrica("Proyectos Totales", lblTotalProyectos, new Color(41, 128, 185)));
        panelTarjetas.add(crearTarjetaMetrica("Clientes Registrados", lblTotalClientes, new Color(39, 174, 96)));
        panelTarjetas.add(crearTarjetaMetrica("Talento Humano", lblTotalDevs, new Color(142, 68, 173)));
        panelTarjetas.add(crearTarjetaMetrica("Ingresos Totales", lblTotalFacturado, new Color(211, 84, 0)));

        panel.add(panelTarjetas, BorderLayout.NORTH);

        // Panel de informacion corporativa y descripcion
        JPanel panelInfo = new JPanel(new GridLayout(1, 2, 20, 0));
        panelInfo.setOpaque(false);

        JPanel cardEmpresa = new JPanel(new BorderLayout());
        cardEmpresa.setBackground(Color.WHITE);
        cardEmpresa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblInfoTitle = new JLabel("Informacion Institucional");
        lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblInfoTitle.setForeground(new Color(44, 62, 80));

        JTextArea txtEmpresa = new JTextArea();
        txtEmpresa.setEditable(false);
        txtEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmpresa.setOpaque(false);
        Empresa emp = service.getEmpresa();
        txtEmpresa.setText(
            "Empresa: " + emp.getNombreComercial() + "\n" +
            "NIT: " + emp.getNit() + "\n" +
            "Direccion: " + emp.getDireccion() + "\n" +
            "Telefono: " + emp.getTelefono() + "\n" +
            "Sitio Web: " + emp.getPaginaWeb() + "\n\n" +
            "Mision: Proporcionar soluciones integrales de software y transformacion\n" +
            "digital bajo los mas altos estandares de ingenieria."
        );

        cardEmpresa.add(lblInfoTitle, BorderLayout.NORTH);
        cardEmpresa.add(txtEmpresa, BorderLayout.CENTER);

        JPanel cardReglas = new JPanel(new BorderLayout());
        cardReglas.setBackground(Color.WHITE);
        cardReglas.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblReglasTitle = new JLabel("Reglas del Negocio Activas");
        lblReglasTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblReglasTitle.setForeground(new Color(44, 62, 80));

        JTextArea txtReglas = new JTextArea();
        txtReglas.setEditable(false);
        txtReglas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtReglas.setOpaque(false);
        txtReglas.setText(
            "• RN1 y RN2: Liquidacion neta con descuento (10%) para clientes frecuentes (>= 3 proyectos).\n" +
            "• RN3: Validacion estricta de solapamiento de fechas y capacidad simultanea de devs.\n" +
            "• RN4 y RN5: Maquina de estados (Pendiente -> Confirmado -> En curso -> Finalizado).\n" +
            "• RN6: Restriccion de contratacion de servicios adicionales a los que esten disponibles.\n" +
            "• RN7: Analisis O(sqrt(N)) de Numero Perfecto sobre telefonos de 10 digitos.\n" +
            "• RN8: Consolidacion de ingresos por fecha de solicitud."
        );

        cardReglas.add(lblReglasTitle, BorderLayout.NORTH);
        cardReglas.add(txtReglas, BorderLayout.CENTER);

        panelInfo.add(cardEmpresa);
        panelInfo.add(cardReglas);

        panel.add(panelInfo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjetaMetrica(String titulo, JLabel valorLabel, Color colorBorde) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, colorBorde),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                new EmptyBorder(15, 15, 15, 15)
            )
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(new Color(127, 140, 141));

        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valorLabel.setForeground(new Color(44, 62, 80));

        card.add(lblTit, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // 2. PANEL PROYECTOS
    // =========================================================================

    private JPanel crearPanelProyectos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabla
        String[] columnas = {
            "Codigo", "Cliente", "Solicitud", "Inicio", "Entrega", "Dias", "Estado", "Metodo Pago", "Devs", "Servicios", "Valor Total"
        };
        modeloProyectos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaProyectos = new JTable(modeloProyectos);
        tablaProyectos.setRowHeight(26);
        tablaProyectos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProyectos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaProyectos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(tablaProyectos), BorderLayout.CENTER);

        // Barra de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnNuevo = new JButton("Nuevo Proyecto");
        JButton btnAsignarDev = new JButton("Asignar Desarrollador (RN3)");
        JButton btnAgregarSrv = new JButton("Agregar Servicio (RN6)");
        JButton btnLiquidar = new JButton("Liquidar Valor Total (RN1/RN2)");
        JButton btnConfirmar = new JButton("Confirmar Proyecto (RN4)");
        JButton btnCambiarEstado = new JButton("Cambiar Estado (RN5)");

        btnNuevo.addActionListener(e -> abrirDialogoNuevoProyecto());
        btnAsignarDev.addActionListener(e -> abrirDialogoAsignarDev());
        btnAgregarSrv.addActionListener(e -> abrirDialogoAgregarServicio());
        btnLiquidar.addActionListener(e -> ejecutarLiquidacionProyecto());
        btnConfirmar.addActionListener(e -> ejecutarConfirmacionProyecto());
        btnCambiarEstado.addActionListener(e -> ejecutarCambioEstadoProyecto());

        panelBotones.add(btnNuevo);
        panelBotones.add(btnAsignarDev);
        panelBotones.add(btnAgregarSrv);
        panelBotones.add(btnLiquidar);
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCambiarEstado);

        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private void abrirDialogoNuevoProyecto() {
        List<Cliente> clientes = service.listarClientes();
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe registrar al menos un cliente antes de crear un proyecto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField txtCodigo = new JTextField("PRJ-" + (service.listarProyectos().size() + 1));
        JTextField txtSolicitud = new JTextField(LocalDate.now().toString());
        JTextField txtInicio = new JTextField(LocalDate.now().plusDays(2).toString());
        JTextField txtEntrega = new JTextField(LocalDate.now().plusDays(22).toString());

        JComboBox<String> cmbClientes = new JComboBox<>();
        for (Cliente c : clientes) {
            cmbClientes.addItem(c.getIdentificacion() + " - " + c.getNombreCompleto());
        }

        JComboBox<MetodoPago> cmbPago = new JComboBox<>(MetodoPago.values());

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.add(new JLabel("Codigo del Proyecto:"));
        form.add(txtCodigo);
        form.add(new JLabel("Cliente Contratante:"));
        form.add(cmbClientes);
        form.add(new JLabel("Fecha Solicitud (AAAA-MM-DD):"));
        form.add(txtSolicitud);
        form.add(new JLabel("Fecha Inicio (AAAA-MM-DD):"));
        form.add(txtInicio);
        form.add(new JLabel("Fecha Entrega (AAAA-MM-DD):"));
        form.add(txtEntrega);
        form.add(new JLabel("Metodo de Pago:"));
        form.add(cmbPago);

        int res = JOptionPane.showConfirmDialog(this, form, "Crear Nuevo Proyecto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String cod = txtCodigo.getText().trim();
                LocalDate fSol = LocalDate.parse(txtSolicitud.getText().trim());
                LocalDate fIni = LocalDate.parse(txtInicio.getText().trim());
                LocalDate fEnt = LocalDate.parse(txtEntrega.getText().trim());
                MetodoPago pago = (MetodoPago) cmbPago.getSelectedItem();

                String itemSel = (String) cmbClientes.getSelectedItem();
                String idCliente = itemSel.split(" - ")[0];

                service.crearProyecto(cod, fSol, fIni, fEnt, pago, idCliente);
                refrescarTodasLasTablas();
                JOptionPane.showMessageDialog(this, "¡Proyecto " + cod + " creado exitosamente en estado PENDIENTE!", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha invalido. Use AAAA-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al crear proyecto", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoAsignarDev() {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un proyecto en la tabla.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String codProy = (String) modeloProyectos.getValueAt(fila, 0);

        List<Desarrollador> devs = service.listarDesarrolladores();
        if (devs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay desarrolladores registrados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> cmbDevs = new JComboBox<>();
        for (Desarrollador d : devs) {
            cmbDevs.addItem(d.getCodigo() + " - " + d.getEquipoDeTrabajo() + " (" + d.getEstado() + " - Max: " + d.getCantidadMaximaProyectosSimultaneos() + ")");
        }

        JPanel form = new JPanel(new GridLayout(2, 1, 5, 5));
        form.add(new JLabel("Seleccione el desarrollador a vincular al proyecto " + codProy + ":"));
        form.add(cmbDevs);

        int res = JOptionPane.showConfirmDialog(this, form, "Asignar Desarrollador (Validacion RN3)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String codDev = ((String) cmbDevs.getSelectedItem()).split(" - ")[0];
            try {
                service.asignarDesarrolladorAProyecto(codProy, codDev);
                refrescarTodasLasTablas();
                JOptionPane.showMessageDialog(this, "Desarrollador " + codDev + " asignado exitosamente.\nSe recalculo el valor del proyecto.", "Asignacion Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Restriccion de Disponibilidad (RN3)", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoAgregarServicio() {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un proyecto en la tabla.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String codProy = (String) modeloProyectos.getValueAt(fila, 0);

        List<ServicioAdicional> srvs = service.listarServicios();
        JComboBox<String> cmbSrvs = new JComboBox<>();
        for (ServicioAdicional s : srvs) {
            cmbSrvs.addItem(s.getCodigo() + " - " + s.getNombre() + " ($" + s.getPrecio() + ") [" + (s.isDisponible() ? "DISPONIBLE" : "NO DISPONIBLE") + "]");
        }

        JPanel form = new JPanel(new GridLayout(2, 1, 5, 5));
        form.add(new JLabel("Seleccione el servicio a contratar para el proyecto " + codProy + ":"));
        form.add(cmbSrvs);

        int res = JOptionPane.showConfirmDialog(this, form, "Agregar Servicio Adicional (RN6)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String codSrv = ((String) cmbSrvs.getSelectedItem()).split(" - ")[0];
            try {
                service.asociarServicioAProyecto(codProy, codSrv);
                refrescarTodasLasTablas();
                JOptionPane.showMessageDialog(this, "Servicio " + codSrv + " incorporado al proyecto.\nSe recalculo el valor total.", "Servicio Asociado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validacion de Servicio (RN6)", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarLiquidacionProyecto() {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proyecto para ver el detalle de liquidacion.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String codProy = (String) modeloProyectos.getValueAt(fila, 0);

        try {
            Proyecto p = service.buscarProyectoPorCodigo(codProy);
            double total = service.calcularValorTotalProyecto(codProy);

            long dias = p.calcularDiasDesarrollo();
            double sumaTarifas = 0;
            StringBuilder sbDevs = new StringBuilder();
            for (Desarrollador d : p.getDesarrolladoresAsignados()) {
                sbDevs.append("  • ").append(d.getCodigo()).append(" (").append(d.getNivel())
                      .append(") -> Tarifa: $").append(String.format("%,.2f", d.getTarifaPorDia())).append("/dia\n");
                sumaTarifas += d.getTarifaPorDia();
            }
            if (p.getDesarrolladoresAsignados().isEmpty()) {
                sbDevs.append("  (Sin desarrolladores asignados)\n");
            }
            double costoDevs = sumaTarifas * dias;

            double costoSrvs = 0;
            StringBuilder sbSrvs = new StringBuilder();
            for (ServicioAdicional s : p.getServiciosUtilizados()) {
                sbSrvs.append("  • ").append(s.getNombre()).append(" -> $").append(String.format("%,.2f", s.getPrecio())).append("\n");
                costoSrvs += s.getPrecio();
            }
            if (p.getServiciosUtilizados().isEmpty()) {
                sbSrvs.append("  (Sin servicios contratados)\n");
            }

            double bruto = costoDevs + costoSrvs;
            boolean esFrecuente = p.getContratante().esClienteFrecuente();
            double descuento = esFrecuente ? bruto * 0.10 : 0.0;

            String mensaje = String.format(
                "===========================================================\n" +
                "         DETALLE DE LIQUIDACION FINANCIERA (RN1 y RN2)      \n" +
                "===========================================================\n" +
                "Proyecto: %s | Estado: %s\n" +
                "Cliente: %s (%s)\n" +
                "Condicion: %s\n\n" +
                "Periodo: %s hasta %s (%d dias de desarrollo)\n\n" +
                "Desarrolladores:\n%s" +
                "Subtotal Equipo: $%,.2f COP\n\n" +
                "Servicios Adicionales:\n%s" +
                "Subtotal Servicios: $%,.2f COP\n\n" +
                "-----------------------------------------------------------\n" +
                "SUBTOTAL BRUTO:               $%,.2f COP\n" +
                "DESCUENTO FRECUENTE (10%%):   -$%,.2f COP\n" +
                "VALOR TOTAL NETO A PAGAR:     $%,.2f COP\n" +
                "===========================================================",
                p.getCodigo(), p.getEstado(), p.getContratante().getNombreCompleto(), p.getContratante().getIdentificacion(),
                esFrecuente ? "CLIENTE FRECUENTE (>=3 Proyectos) -> APLICA DESCUENTO 10%" : "CLIENTE ESTANDAR (Sin descuento)",
                p.getFechaInicio(), p.getFechaEntrega(), dias,
                sbDevs.toString(), costoDevs,
                sbSrvs.toString(), costoSrvs,
                bruto, descuento, total
            );

            JTextArea txt = new JTextArea(mensaje);
            txt.setEditable(false);
            txt.setFont(new Font("Consolas", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(this, new JScrollPane(txt), "Liquidacion del Proyecto " + codProy, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al liquidar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarConfirmacionProyecto() {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proyecto para confirmar.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String codProy = (String) modeloProyectos.getValueAt(fila, 0);

        try {
            service.confirmarProyecto(codProy);
            refrescarTodasLasTablas();
            JOptionPane.showMessageDialog(this,
                "¡Proyecto " + codProy + " CONFIRMADO exitosamente!\n" +
                "Se actualizo el estado operativo de los desarrolladores asignados segun su carga (RN4).",
                "Proyecto Confirmado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Confirmar (RN4/RN5)", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarCambioEstadoProyecto() {
        int fila = tablaProyectos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proyecto para cambiar su estado.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String codProy = (String) modeloProyectos.getValueAt(fila, 0);

        JComboBox<EstadoProyecto> cmbEstados = new JComboBox<>(new EstadoProyecto[]{
            EstadoProyecto.CONFIRMADO, EstadoProyecto.EN_CURSO, EstadoProyecto.FINALIZADO, EstadoProyecto.CANCELADO
        });

        int res = JOptionPane.showConfirmDialog(this, cmbEstados, "Seleccione nuevo estado para " + codProy + " (RN5):", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            EstadoProyecto nuevo = (EstadoProyecto) cmbEstados.getSelectedItem();
            try {
                service.cambiarEstadoProyecto(codProy, nuevo);
                refrescarTodasLasTablas();
                JOptionPane.showMessageDialog(this, "Estado del proyecto actualizado a: " + nuevo, "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Transicion Invalida (RN5)", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 3. PANEL CLIENTES
    // =========================================================================

    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnas = {"Identificacion / NIT", "Nombre Completo / Razon Social", "Telefono", "Correo", "Pais", "Proyectos Contratados", "Cliente Frecuente (RN1)"};
        modeloClientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setRowHeight(25);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Registrar Nuevo Cliente");
        btnNuevo.addActionListener(e -> {
            JTextField txtId = new JTextField();
            JTextField txtNombre = new JTextField();
            JTextField txtTel = new JTextField();
            JTextField txtEmail = new JTextField();
            JTextField txtPais = new JTextField("Colombia");

            JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
            form.add(new JLabel("Identificacion / NIT:")); form.add(txtId);
            form.add(new JLabel("Nombre o Razon Social:")); form.add(txtNombre);
            form.add(new JLabel("Telefono:")); form.add(txtTel);
            form.add(new JLabel("Correo Electronico:")); form.add(txtEmail);
            form.add(new JLabel("Pais de Procedencia:")); form.add(txtPais);

            int op = JOptionPane.showConfirmDialog(this, form, "Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION);
            if (op == JOptionPane.OK_OPTION) {
                try {
                    Cliente c = new Cliente(txtId.getText().trim(), txtNombre.getText().trim(), txtTel.getText().trim(), txtEmail.getText().trim(), txtPais.getText().trim());
                    service.registrarCliente(c);
                    refrescarTodasLasTablas();
                    JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panelBotones.add(btnNuevo);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // 4. PANEL DESARROLLADORES
    // =========================================================================

    private JPanel crearPanelDesarrolladores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnas = {"Codigo", "Equipo de Trabajo", "Nivel", "Tarifa / Dia", "Max Proyectos Simultaneos", "Estado Operativo", "Proyectos Asignados"};
        modeloDevs = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDevs = new JTable(modeloDevs);
        tablaDevs.setRowHeight(25);
        tablaDevs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDevs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tablaDevs), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Registrar Desarrollador");
        btnNuevo.addActionListener(e -> {
            JTextField txtCod = new JTextField();
            JTextField txtEquipo = new JTextField();
            JComboBox<NivelDesarrollador> cmbNivel = new JComboBox<>(NivelDesarrollador.values());
            JTextField txtMax = new JTextField("2");
            JTextField txtTarifa = new JTextField("150000");
            JComboBox<EstadoDesarrollador> cmbEstado = new JComboBox<>(EstadoDesarrollador.values());

            JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
            form.add(new JLabel("Codigo:")); form.add(txtCod);
            form.add(new JLabel("Equipo:")); form.add(txtEquipo);
            form.add(new JLabel("Nivel:")); form.add(cmbNivel);
            form.add(new JLabel("Max Simultaneos:")); form.add(txtMax);
            form.add(new JLabel("Tarifa por dia (COP):")); form.add(txtTarifa);
            form.add(new JLabel("Estado Inicial:")); form.add(cmbEstado);

            int op = JOptionPane.showConfirmDialog(this, form, "Nuevo Desarrollador", JOptionPane.OK_CANCEL_OPTION);
            if (op == JOptionPane.OK_OPTION) {
                try {
                    int max = Integer.parseInt(txtMax.getText().trim());
                    double tarifa = Double.parseDouble(txtTarifa.getText().trim());
                    Desarrollador d = new Desarrollador(txtCod.getText().trim(), txtEquipo.getText().trim(),
                            (NivelDesarrollador) cmbNivel.getSelectedItem(), max, tarifa, (EstadoDesarrollador) cmbEstado.getSelectedItem());
                    service.registrarDesarrollador(d);
                    refrescarTodasLasTablas();
                    JOptionPane.showMessageDialog(this, "Desarrollador registrado exitosamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panelBotones.add(btnNuevo);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // 5. PANEL SERVICIOS ADICIONALES
    // =========================================================================

    private JPanel crearPanelServicios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnas = {"Codigo", "Nombre del Servicio", "Precio", "Disponible (RN6)", "Descripcion"};
        modeloServicios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaServicios = new JTable(modeloServicios);
        tablaServicios.setRowHeight(25);
        tablaServicios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaServicios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tablaServicios), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Registrar Servicio");
        JButton btnToggle = new JButton("Alternar Disponibilidad (Habilitar / Inhabilitar)");

        btnNuevo.addActionListener(e -> {
            JTextField txtCod = new JTextField();
            JTextField txtNom = new JTextField();
            JTextField txtDesc = new JTextField();
            JTextField txtPrecio = new JTextField("1000000");
            JCheckBox chkDisp = new JCheckBox("Disponible para contratacion", true);

            JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
            form.add(new JLabel("Codigo:")); form.add(txtCod);
            form.add(new JLabel("Nombre:")); form.add(txtNom);
            form.add(new JLabel("Descripcion:")); form.add(txtDesc);
            form.add(new JLabel("Precio (COP):")); form.add(txtPrecio);
            form.add(new JLabel("Disponibilidad:")); form.add(chkDisp);

            int op = JOptionPane.showConfirmDialog(this, form, "Nuevo Servicio Adicional", JOptionPane.OK_CANCEL_OPTION);
            if (op == JOptionPane.OK_OPTION) {
                try {
                    double precio = Double.parseDouble(txtPrecio.getText().trim());
                    ServicioAdicional s = new ServicioAdicional(txtCod.getText().trim(), txtNom.getText().trim(),
                            txtDesc.getText().trim(), precio, chkDisp.isSelected());
                    service.registrarServicioAdicional(s);
                    refrescarTodasLasTablas();
                    JOptionPane.showMessageDialog(this, "Servicio registrado exitosamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnToggle.addActionListener(e -> {
            int fila = tablaServicios.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un servicio de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String cod = (String) modeloServicios.getValueAt(fila, 0);
            try {
                ServicioAdicional s = service.buscarServicioPorCodigo(cod);
                s.setDisponible(!s.isDisponible());
                refrescarTodasLasTablas();
                JOptionPane.showMessageDialog(this, "Disponibilidad del servicio " + cod + " cambiada a: " + (s.isDisponible() ? "DISPONIBLE" : "INACTIVO"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnNuevo);
        panelBotones.add(btnToggle);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // 6. PANEL FUNCIONALIDADES ESPECIALES (RN7 y RN8)
    // =========================================================================

    private JPanel crearPanelEspeciales() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));

        // Subpanel Izquierdo: RN7 (Numero Perfecto)
        JPanel panelRN7 = new JPanel(new BorderLayout(10, 10));
        panelRN7.setBackground(Color.WHITE);
        panelRN7.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitRN7 = new JLabel("RN7: Verificacion de Numero Perfecto sobre Telefono");
        lblTitRN7.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitRN7.setForeground(new Color(41, 128, 185));

        JPanel panelInputRN7 = new JPanel(new BorderLayout(5, 5));
        panelInputRN7.setOpaque(false);
        JTextField txtTel = new JTextField("8589869056");
        txtTel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnVerificarRN7 = new JButton("Consultar y Analizar");
        btnVerificarRN7.setFont(new Font("Segoe UI", Font.BOLD, 12));

        panelInputRN7.add(new JLabel("Numero de Telefono del Cliente:"), BorderLayout.NORTH);
        panelInputRN7.add(txtTel, BorderLayout.CENTER);
        panelInputRN7.add(btnVerificarRN7, BorderLayout.EAST);

        JTextArea txtResultadoRN7 = new JTextArea();
        txtResultadoRN7.setEditable(false);
        txtResultadoRN7.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResultadoRN7.setBackground(new Color(248, 249, 250));
        txtResultadoRN7.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 236)));

        JPanel panelPruebasRapidas = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelPruebasRapidas.setOpaque(false);
        JButton btnSemilla1 = new JButton("Probar 8589869056 (Semilla Perfecto)");
        JButton btnSemilla2 = new JButton("Probar 3154879201 (Semilla No Perfecto)");

        btnSemilla1.addActionListener(e -> {
            txtTel.setText("8589869056");
            btnVerificarRN7.doClick();
        });
        btnSemilla2.addActionListener(e -> {
            txtTel.setText("3154879201");
            btnVerificarRN7.doClick();
        });

        panelPruebasRapidas.add(btnSemilla1);
        panelPruebasRapidas.add(btnSemilla2);

        btnVerificarRN7.addActionListener(e -> {
            String tel = txtTel.getText().trim();
            if (tel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite un numero telefonico.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Cliente c = service.buscarClientePorTelefono(tel);
                long inicioNano = System.nanoTime();
                boolean esPerf = service.verificarTelefonoClienteNumeroPerfecto(tel);
                long tiempoNano = System.nanoTime() - inicioNano;
                double tiempoMs = tiempoNano / 1_000_000.0;
                long numLong = ValidadorUtil.parsearTelefonoALong(c.getTelefono());

                String res = String.format(
                    "====================================================\n" +
                    "           EVALUACION DE NUMERO PERFECTO (RN7)       \n" +
                    "====================================================\n" +
                    "Cliente Encontrado:\n" +
                    "  • ID/NIT: %s\n" +
                    "  • Nombre / Razon: %s\n" +
                    "  • Telefono: %s\n" +
                    "  • Correo: %s\n" +
                    "  • Pais: %s\n\n" +
                    "Analisis Matematico O(sqrt(N)):\n" +
                    "  • Valor numerico evaluado (long): %d\n" +
                    "  • Tiempo de computo: %.4f ms\n\n" +
                    "DICTAMEN:\n" +
                    "  %s\n" +
                    "====================================================",
                    c.getIdentificacion(), c.getNombreCompleto(), c.getTelefono(), c.getCorreoElectronico(), c.getPaisProcedencia(),
                    numLong, tiempoMs,
                    esPerf ? ">>> ¡EL TELEFONO ES UN NUMERO PERFECTO! <<<" : ">>> EL TELEFONO NO ES UN NUMERO PERFECTO <<<"
                );
                txtResultadoRN7.setText(res);
            } catch (Exception ex) {
                txtResultadoRN7.setText("ERROR:\n" + ex.getMessage());
            }
        });

        JPanel panelSuperiorRN7 = new JPanel(new BorderLayout(5, 5));
        panelSuperiorRN7.setOpaque(false);
        panelSuperiorRN7.add(lblTitRN7, BorderLayout.NORTH);
        panelSuperiorRN7.add(panelInputRN7, BorderLayout.CENTER);
        panelSuperiorRN7.add(panelPruebasRapidas, BorderLayout.SOUTH);

        panelRN7.add(panelSuperiorRN7, BorderLayout.NORTH);
        panelRN7.add(new JScrollPane(txtResultadoRN7), BorderLayout.CENTER);

        // Subpanel Derecho: RN8 (Ingresos por Fecha)
        JPanel panelRN8 = new JPanel(new BorderLayout(10, 10));
        panelRN8.setBackground(Color.WHITE);
        panelRN8.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitRN8 = new JLabel("RN8: Total de Ingresos por Fecha de Solicitud");
        lblTitRN8.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitRN8.setForeground(new Color(39, 174, 96));

        JPanel panelInputRN8 = new JPanel(new BorderLayout(5, 5));
        panelInputRN8.setOpaque(false);
        JTextField txtFecha = new JTextField(LocalDate.now().toString());
        txtFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnCalcularRN8 = new JButton("Consultar Ingresos");
        btnCalcularRN8.setFont(new Font("Segoe UI", Font.BOLD, 12));

        panelInputRN8.add(new JLabel("Fecha de Solicitud (AAAA-MM-DD):"), BorderLayout.NORTH);
        panelInputRN8.add(txtFecha, BorderLayout.CENTER);
        panelInputRN8.add(btnCalcularRN8, BorderLayout.EAST);

        JLabel lblMontoGrande = new JLabel("$0,00 COP", SwingConstants.CENTER);
        lblMontoGrande.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMontoGrande.setForeground(new Color(39, 174, 96));
        lblMontoGrande.setBorder(new EmptyBorder(10, 0, 10, 0));

        DefaultTableModel modeloFiltrados = new DefaultTableModel(new String[]{"Codigo", "Cliente", "Estado", "Valor Total"}, 0);
        JTable tablaFiltrados = new JTable(modeloFiltrados);
        tablaFiltrados.setRowHeight(24);

        btnCalcularRN8.addActionListener(e -> {
            try {
                LocalDate f = LocalDate.parse(txtFecha.getText().trim());
                double total = service.calcularIngresosPorFecha(f);
                lblMontoGrande.setText(String.format("$%,.2f COP", total));

                modeloFiltrados.setRowCount(0);
                for (Proyecto p : service.listarProyectos()) {
                    if (f.equals(p.getFechaSolicitud()) && p.getEstado() != EstadoProyecto.CANCELADO) {
                        modeloFiltrados.addRow(new Object[]{
                            p.getCodigo(),
                            p.getContratante().getNombreCompleto(),
                            p.getEstado(),
                            String.format("$%,.2f", p.getValorTotal())
                        });
                    }
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha invalido. Use AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelSuperiorRN8 = new JPanel(new BorderLayout(5, 5));
        panelSuperiorRN8.setOpaque(false);
        panelSuperiorRN8.add(lblTitRN8, BorderLayout.NORTH);
        panelSuperiorRN8.add(panelInputRN8, BorderLayout.CENTER);
        panelSuperiorRN8.add(lblMontoGrande, BorderLayout.SOUTH);

        panelRN8.add(panelSuperiorRN8, BorderLayout.NORTH);
        panelRN8.add(new JScrollPane(tablaFiltrados), BorderLayout.CENTER);

        panel.add(panelRN7);
        panel.add(panelRN8);

        // Ejecutar calculo inicial para la fecha actual
        SwingUtilities.invokeLater(btnCalcularRN8::doClick);

        return panel;
    }

    // =========================================================================
    // METODOS DE SINCRONIZACION Y REFRESCO DE DATOS
    // =========================================================================

    public void refrescarTodasLasTablas() {
        // 1. Proyectos
        modeloProyectos.setRowCount(0);
        double granTotalFacturado = 0.0;
        int proyectosActivos = 0;

        for (Proyecto p : service.listarProyectos()) {
            granTotalFacturado += p.getValorTotal();
            if (p.getEstado() != EstadoProyecto.FINALIZADO && p.getEstado() != EstadoProyecto.CANCELADO) {
                proyectosActivos++;
            }

            modeloProyectos.addRow(new Object[]{
                p.getCodigo(),
                p.getContratante() != null ? p.getContratante().getNombreCompleto() : "N/A",
                p.getFechaSolicitud(),
                p.getFechaInicio(),
                p.getFechaEntrega(),
                p.calcularDiasDesarrollo(),
                p.getEstado(),
                p.getMetodoPago(),
                p.getDesarrolladoresAsignados().size(),
                p.getServiciosUtilizados().size(),
                String.format("$%,.2f", p.getValorTotal())
            });
        }

        // 2. Clientes
        modeloClientes.setRowCount(0);
        for (Cliente c : service.listarClientes()) {
            modeloClientes.addRow(new Object[]{
                c.getIdentificacion(),
                c.getNombreCompleto(),
                c.getTelefono(),
                c.getCorreoElectronico(),
                c.getPaisProcedencia(),
                c.getProyectosContratados().size(),
                c.esClienteFrecuente() ? "SI (10% Desc.)" : "NO"
            });
        }

        // 3. Desarrolladores
        modeloDevs.setRowCount(0);
        for (Desarrollador d : service.listarDesarrolladores()) {
            modeloDevs.addRow(new Object[]{
                d.getCodigo(),
                d.getEquipoDeTrabajo(),
                d.getNivel(),
                String.format("$%,.2f", d.getTarifaPorDia()),
                d.getCantidadMaximaProyectosSimultaneos(),
                d.getEstado(),
                d.getProyectosAsignados().size()
            });
        }

        // 4. Servicios
        modeloServicios.setRowCount(0);
        for (ServicioAdicional s : service.listarServicios()) {
            modeloServicios.addRow(new Object[]{
                s.getCodigo(),
                s.getNombre(),
                String.format("$%,.2f", s.getPrecio()),
                s.isDisponible() ? "SI" : "NO",
                s.getDescripcion()
            });
        }

        // 5. Tarjetas del Dashboard
        if (lblTotalProyectos != null) {
            lblTotalProyectos.setText(String.valueOf(service.listarProyectos().size()));
            lblTotalClientes.setText(String.valueOf(service.listarClientes().size()));
            lblTotalDevs.setText(String.valueOf(service.listarDesarrolladores().size()));
            lblTotalFacturado.setText(String.format("$%,.2f", granTotalFacturado));
        }
    }

    /**
     * Permite lanzar la interfaz de forma independiente como aplicacion de escritorio.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DevPlusService service = DatosPruebaUtil.crearServicioConDatosPrueba();
            VentanaPrincipal ventana = new VentanaPrincipal(service);
            ventana.setVisible(true);
        });
    }
}
