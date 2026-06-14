package ru.itmo.sofi.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.itmo.sofi.base.CollectionStorage;
import ru.itmo.sofi.base.JsonCollectionStorage;
import ru.itmo.sofi.command.Load;
import ru.itmo.sofi.command.Save;
import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.booking.BookingStatus;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.checkout.ReturnCondition;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.essence.instrument.InstrumentStatus;
import ru.itmo.sofi.essence.instrument.InstrumentType;
import ru.itmo.sofi.essence.user.User;
import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.login.CurrentUser;
import ru.itmo.sofi.service.BookingService;
import ru.itmo.sofi.service.CheckoutService;
import ru.itmo.sofi.service.InstrumentService;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ru.itmo.sofi.exception.UserInputException;
import javafx.util.Callback;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ru.itmo.sofi.service.UserService;
import ru.itmo.sofi.exception.StorageSaveException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MainController {
    private boolean criticalLoadError = false;
    private String ownerUsername;
    private boolean instrumentsInitialized = false;
    private final InstrumentService instrumentService = new InstrumentService();
    private final BookingService bookingService = new BookingService(instrumentService);
    private final CheckoutService checkoutService = new CheckoutService(instrumentService, bookingService);
    private final CollectionStorage<Instrument> instrumentStorage = new JsonCollectionStorage<Instrument>(new TypeReference<Set<Instrument>>() {
    });
    private final CollectionStorage<Booking> bookingStorage = new JsonCollectionStorage<Booking>(new TypeReference<Set<Booking>>() {
    });
    private final CollectionStorage<Checkout> checkoutStorage = new JsonCollectionStorage<Checkout>(new TypeReference<Set<Checkout>>() {
    });
    private final Save saveCommand = new Save(instrumentService, bookingService, checkoutService, instrumentStorage, bookingStorage, checkoutStorage);
    private final Load loadCommand = new Load(instrumentService, bookingService, checkoutService, instrumentStorage, bookingStorage, checkoutStorage);
    private final UserService userService = new UserService();
    @FXML
    private Label welcomeText;
    @FXML
    private TableView<Instrument> instrumentTable;
    @FXML
    private TableColumn<Instrument, Long> instrumentTId;
    @FXML
    private TableColumn<Instrument, String> instrumentTName;
    @FXML
    private TableColumn<Instrument, InstrumentType> instrumentTType;
    @FXML
    private TableColumn<Instrument, String> instrumentTInvNumber;
    @FXML
    private TableColumn<Instrument, String> instrumentTLocation;
    @FXML
    private TableColumn<Instrument, InstrumentStatus> instrumentTStatus;
    @FXML
    private TableColumn<Instrument, String> instrumentTOwner;
    @FXML
    private TableColumn<Booking, Instant> bookingTCreated;
    @FXML
    private TableColumn<Instrument, Instant> bookingTUpdated;
    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TableColumn<Booking, Long> bookingTId;
    @FXML
    private TableColumn<Booking, Long> bookingTInstId;
    @FXML
    private TableColumn<Booking, Instant> bookingTStart;
    @FXML
    private TableColumn<Booking, Instant> bookingTEnd;
    @FXML
    private TableColumn<Booking, BookingStatus> bookingTStatus;
    @FXML
    private TableColumn<Instrument, String> bookingTOwner;
    @FXML
    private TableColumn<Booking, Instant> instrumentTCreated;
    @FXML
    private TableColumn<Instrument, Instant> instrumentTUpdated;
    @FXML
    private ComboBox<Instrument> bookingInstrumentFilterBox;
    @FXML
    private TableView<Checkout> checkoutTable;
    @FXML
    private TableColumn<Checkout, Long> checkoutTId;
    @FXML
    private TableColumn<Checkout, Long> checkoutTInstId;
    @FXML
    private TableColumn<Checkout, String> checkoutTUser;
    @FXML
    private TableColumn<Checkout, String> checkoutTComm;
    @FXML
    private TableColumn<Checkout, Instant> checkoutTTaken;
    @FXML
    private TableColumn<Checkout, Instant> checkoutTReturned;
    @FXML
    private TableColumn<Checkout, ReturnCondition> checkoutTCondition;
    @FXML
    private TableColumn<Checkout, String> checkoutTOwner;
    @FXML
    private TableColumn<Checkout, Instant> checkoutTCreated;
    @FXML
    private ComboBox<Instrument> checkoutInstrumentFilterBox;
    @FXML
    private Button deleteCheckoutButton;
    @FXML
    private Button deleteInstrumentButton;
    @FXML
    private Button updateInstrumentButton;
    @FXML
    private Button showUsersButton1;
    @FXML
    private Button showUsersButton2;
    @FXML
    private Button showUsersButton3;

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Добро пожаловать в запись!");
    }

    @FXML
    private void onRefreshInstrument() {
        System.out.println("Refresh приборов нажат");
//        if (!instrumentsInitialized) {
//            instrumentService.add("pH-метр", InstrumentType.PH_METER, "123", "Лаборатория", InstrumentStatus.OUT_OF_SERVICE, "SYSTEM");
//            instrumentService.add("сушка", InstrumentType.DRYING_OVEN, "123", "Лаборатория", InstrumentStatus.ACTIVE, "SYSTEM");
//            instrumentsInitialized = true;
//        }
        instrumentTable.setItems(FXCollections.observableArrayList(instrumentService.getAll()));
        bookingInstrumentFilterBox.getItems().setAll(instrumentService.getAll());
        checkoutInstrumentFilterBox.getItems().setAll(instrumentService.getAll());
    }

    @FXML
    private void onRefreshBooking() {
//        bookingTable.setItems(FXCollections.observableArrayList(bookingService.getAll()));
        Instrument selected = bookingInstrumentFilterBox.getValue();
        Set<Booking> bookings = bookingService.getAll();
        if (selected != null) {
            Set<Booking> filtered = new HashSet<Booking>();
            for (Booking booking : bookings) {
                if (booking.getInstrumentId() == selected.getId()) {
                    filtered.add(booking);
                }
            }
            bookingTable.setItems(FXCollections.observableArrayList(filtered));
        } else {
            bookingTable.setItems(FXCollections.observableArrayList(bookings));
        }
    }

    @FXML
    private void onCreateInstrument() {
        if (!CurrentUser.isAdmin()) {
            showError("Создание доступно только администратору.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Создать прибор");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField nameField = new TextField();
        TextField inventoryField = new TextField();
        TextField locationField = new TextField();
        ComboBox<InstrumentType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(InstrumentType.values());
        ComboBox<InstrumentStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(InstrumentStatus.values());
        statusBox.setValue(InstrumentStatus.ACTIVE);
        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Тип:"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Инв. номер:"), 0, 2);
        grid.add(inventoryField, 1, 2);
        grid.add(new Label("Локация:"), 0, 3);
        grid.add(locationField, 1, 3);
        grid.add(new Label("Статус:"), 0, 4);
        grid.add(statusBox, 1, 4);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    instrumentService.add(nameField.getText().trim(), typeBox.getValue(), inventoryField.getText().trim(), locationField.getText().trim(), statusBox.getValue(), CurrentUser.getCurrentUser().getLogin());
                    onRefreshInstrument();
                } catch (UserInputException e) {
                    showError(e.getMessage());
                    event.consume();
                }
            }
        });

        dialog.showAndWait();;
    }

    @FXML
    private void onDeleteInstrument() {
        Instrument selected = instrumentTable.getSelectionModel().getSelectedItem();
        if (!CurrentUser.isAdmin()) {
            showError("Удаление доступно только администратору.");
            return;
        }
        if (selected == null) {
            showError("Выберите прибор в таблице.");
            return;
        }
        try {
            instrumentService.remove(selected.getId());
            onRefreshInstrument();
        } catch (UserInputException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onUpdateInstrument() {
        Instrument selected = instrumentTable.getSelectionModel().getSelectedItem();
        if (!CurrentUser.isAdmin()) {
            showError("Удаление доступно только администратору.");
            return;
        }
        if (selected == null) {
            showError("Выберите прибор");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Изменить прибор");
        TextField nameField = new TextField(selected.getName());
        TextField invField = new TextField(selected.getInventoryNumber());
        TextField locField = new TextField(selected.getLocation());
        ComboBox<InstrumentStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(InstrumentStatus.values());
        statusBox.setValue(selected.getStatus());
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Инв. номер:"), 0, 2);
        grid.add(invField, 1, 2);
        grid.add(new Label("Место:"), 0, 3);
        grid.add(locField, 1, 3);
        grid.add(new Label("Статус:"), 0, 4);
        grid.add(statusBox, 1, 4);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                instrumentService.update(selected.getId(), nameField.getText().trim(), invField.getText().trim(), locField.getText().trim(), statusBox.getValue(), CurrentUser.getCurrentUser().getLogin());
                onRefreshInstrument();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    private void onRefreshCheckout() {
//        checkoutTable.setItems(FXCollections.observableArrayList(checkoutService.getAll()));
        Instrument selected = checkoutInstrumentFilterBox.getValue();
        Set<Checkout> checkouts = checkoutService.getAll();
        if (selected != null) {
            Set<Checkout> filtered = new HashSet<Checkout>();
            for (Checkout checkout : checkouts) {
                if (checkout.getInstrumentId() == selected.getId()) {
                    filtered.add(checkout);
                }
            }
            checkoutTable.setItems(FXCollections.observableArrayList(filtered));
        } else {
            checkoutTable.setItems(FXCollections.observableArrayList(checkouts));
        }
    }

    @FXML
    private void onCreateBooking() throws UserInputException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Создать бронирование");
        ComboBox<Instrument> instrumentBox = new ComboBox<>();
        instrumentBox.getItems().addAll(instrumentService.getAll());
        instrumentBox.setCellFactory(new Callback<ListView<Instrument>, ListCell<Instrument>>() {
            @Override
            public ListCell<Instrument> call(ListView<Instrument> param) {
                return new ListCell<Instrument>() {
                    @Override
                    protected void updateItem(Instrument item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName() + " - " + item.getType() + " - " + item.getStatus());
                        }
                    }
                };
            }
        });
        instrumentBox.setButtonCell(new ListCell<Instrument>() {
            @Override
            protected void updateItem(Instrument item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getType() + " - " + item.getStatus());
                }
            }
        });
        TextField startField = new TextField();
        startField.setPromptText("2026-05-02 12:00");
        TextField endField = new TextField();
        endField.setPromptText("2026-05-02 14:00");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Прибор:"), 0, 0);
        grid.add(instrumentBox, 1, 0);
        grid.add(new Label("Начало:"), 0, 1);
        grid.add(startField, 1, 1);
        grid.add(new Label("Конец:"), 0, 2);
        grid.add(endField, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Instrument selected = instrumentBox.getValue();
                    if (selected == null) {
                        throw new UserInputException("Выберите прибор.");
                    }
                    bookingService.bookCreate(selected.getId(), startField.getText(), endField.getText(), CurrentUser.getCurrentUser().getLogin());
                } catch (UserInputException e) {
                    showError(e.getMessage());
                    event.consume();
                }
            }
        });
        dialog.showAndWait();
        onRefreshBooking();
//        Optional<ButtonType> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            if (result.get() == ButtonType.OK) {
//                try {
//                    Instrument selected = instrumentBox.getValue();
//                    if (selected == null) {
//                        throw new UserInputException("Выберите прибор.");
//                    }
//                    bookingService.bookCreate(selected.getId(), startField.getText(), endField.getText());
//                } catch (UserInputException e) {
//                    showError(e.getMessage());
//                }
//            }
//        }
    }

    @FXML
    private void onCancelBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите бронь в таблице.");
            return;
        }
        if (!CurrentUser.isAdmin() && !selected.getOwnerUsername().equals(CurrentUser.getCurrentUser().getLogin())) {
            showError("Вы можете отменить только своё бронирование.");
            return;
        }
        try {
            bookingService.bookCancel(selected.getId());
            onRefreshBooking();
        } catch (UserInputException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onCreateCheckout() throws UserInputException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Выдать прибор");
        ComboBox<Instrument> instrumentBox = new ComboBox<>();
        instrumentBox.getItems().addAll(instrumentService.getAll());
        instrumentBox.setCellFactory(new Callback<ListView<Instrument>, ListCell<Instrument>>() {
            @Override
            public ListCell<Instrument> call(ListView<Instrument> param) {
                return new ListCell<Instrument>() {
                    @Override
                    protected void updateItem(Instrument item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName() + " - " + item.getType() + " - " + item.getStatus());
                        }
                    }
                };
            }
        });
        instrumentBox.setButtonCell(new ListCell<Instrument>() {
            @Override
            protected void updateItem(Instrument item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getType() + " - " + item.getStatus());
                }
            }
        });
        TextField userField = new TextField();
        userField.setPromptText("ФИО или логин пользователя");
        TextField commentField = new TextField();
        commentField.setPromptText("Комментарий");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Прибор:"), 0, 0);
        grid.add(instrumentBox, 1, 0);
        grid.add(new Label("Пользователь:"), 0, 1);
        grid.add(userField, 1, 1);
        grid.add(new Label("Комментарий:"), 0, 2);
        grid.add(commentField, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Instrument selected = instrumentBox.getValue();
                    if (selected == null) {
                        throw new UserInputException("Выберите прибор.");
                    }
                    checkoutService.checkoutTake(selected.getId(), userField.getText(), commentField.getText(), CurrentUser.getCurrentUser().getLogin());
                } catch (UserInputException e) {
                    showError(e.getMessage());
                    event.consume();
                }
            }
        });
        dialog.showAndWait();
        onRefreshCheckout();
//        Optional<ButtonType> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            if (result.get() == ButtonType.OK) {
//                try {
//                    Instrument selected = instrumentBox.getValue();
//
//                    if (selected == null) {
//                        throw new UserInputException("Выберите прибор.");
//                    }
//                    checkoutService.checkoutTake(selected.getId(), userField.getText(), commentField.getText());
//                } catch (UserInputException e) {
//                    showError(e.getMessage());
//                }
//            }
//        }
    }

    @FXML
    private void onClearBookingFilter() {
        bookingInstrumentFilterBox.setValue(null);
        onRefreshBooking();
    }

    @FXML
    private void onClearCheckoutFilter() {
        checkoutInstrumentFilterBox.setValue(null);
        onRefreshCheckout();
    }

    @FXML
    private void onReturnCheckout() throws UserInputException {
        System.out.println("Кнопка возврата нажата");
        Checkout selected = checkoutTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите выдачу в таблице.");
            return;
        }
        if (!CurrentUser.isAdmin() && !selected.getOwnerUsername().equals(CurrentUser.getCurrentUser().getLogin())) {
            showError("Вы можете вернуть только свою выдачу.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Возврат прибора");
        ComboBox<ReturnCondition> conditionBox = new ComboBox<ReturnCondition>();
        conditionBox.getItems().addAll(ReturnCondition.values());
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Состояние прибора:"), 0, 0);
        grid.add(conditionBox, 1, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                try {
                    ReturnCondition condition = conditionBox.getValue();
                    if (condition == null) {
                        throw new UserInputException("Выберите состояние прибора.");
                    }
                    checkoutService.checkoutReturn(selected.getId(), condition.name());
                    onRefreshCheckout();
                } catch (UserInputException e) {
                    showError(e.getMessage());
                }
            }
        }
        onRefreshCheckout();
    }

    @FXML
    private void onDeleteCheckout() {
        Checkout selected = checkoutTable.getSelectionModel().getSelectedItem();
        if (!CurrentUser.isAdmin()) {
            showError("Удаление доступно только администратору.");
            return;
        }
        if (selected == null) {
            showError("Выберите выдачу в таблице.");
            return;
        }
        try {
            checkoutService.remove(selected.getId());
            onRefreshCheckout();
        } catch (UserInputException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        if (userService.getAllUsers().isEmpty()) {
            userService.registration("administrator", "administrator", true);
            userService.registration("simpleuser", "simpleuser", false);
        }
        try {
//            userService.loadUsers(HelloApplication.DATA_FOLDER);
            Path instrumentsPath = HelloApplication.DATA_FOLDER.resolve("instruments.json");
            Path bookingsPath = HelloApplication.DATA_FOLDER.resolve("bookings.json");
            Path checkoutsPath = HelloApplication.DATA_FOLDER.resolve("checkouts.json");
            if (Files.exists(instrumentsPath) && Files.exists(bookingsPath) && Files.exists(checkoutsPath)) {
                loadCommand.execute(new String[]{"load", HelloApplication.DATA_FOLDER.toString()});
            }
        } catch (StorageLoadException e) {
            criticalLoadError = true;
            showError(e.getMessage());
        }
        updateAccessRights();
        onRefreshInstrument();
        onRefreshBooking();
        onRefreshCheckout();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        instrumentTId.setCellValueFactory(new PropertyValueFactory<>("id"));
        instrumentTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        instrumentTType.setCellValueFactory(new PropertyValueFactory<>("type"));
        instrumentTInvNumber.setCellValueFactory(new PropertyValueFactory<>("inventoryNumber"));
        instrumentTLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        instrumentTStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        instrumentTOwner.setCellValueFactory(new PropertyValueFactory<>("ownerUsername"));
        instrumentTCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        instrumentTCreated.setCellFactory(createInstantCellFactory(formatter));
        instrumentTUpdated.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        instrumentTUpdated.setCellFactory(createInstantCellFactory(formatter));
        instrumentTId.setStyle("-fx-alignment: CENTER;");
        instrumentTName.setStyle("-fx-alignment: CENTER;");
        instrumentTType.setStyle("-fx-alignment: CENTER;");
        instrumentTInvNumber.setStyle("-fx-alignment: CENTER;");
        instrumentTLocation.setStyle("-fx-alignment: CENTER;");
        instrumentTStatus.setStyle("-fx-alignment: CENTER;");
        instrumentTOwner.setStyle("-fx-alignment: CENTER;");
        instrumentTCreated.setStyle("-fx-alignment: CENTER;");
        instrumentTUpdated.setStyle("-fx-alignment: CENTER;");
        bookingTId.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookingTInstId.setCellValueFactory(new PropertyValueFactory<>("instrumentId"));
        bookingTStart.setCellValueFactory(new PropertyValueFactory<>("startAt"));
        bookingTStart.setCellFactory(createInstantCellFactory(formatter));
        bookingTEnd.setCellValueFactory(new PropertyValueFactory<>("endAt"));
        bookingTEnd.setCellFactory(createInstantCellFactory(formatter));
        bookingTStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookingTOwner.setCellValueFactory(new PropertyValueFactory<>("ownerUsername"));
        bookingTCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        bookingTCreated.setCellFactory(createInstantCellFactory(formatter));
        bookingTUpdated.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        bookingTUpdated.setCellFactory(createInstantCellFactory(formatter));
        bookingTId.setStyle("-fx-alignment: CENTER;");
        bookingTInstId.setStyle("-fx-alignment: CENTER;");
        bookingTStart.setStyle("-fx-alignment: CENTER;");
        bookingTEnd.setStyle("-fx-alignment: CENTER;");
        bookingTStatus.setStyle("-fx-alignment: CENTER;");
        instrumentTStatus.setStyle("-fx-alignment: CENTER;");
        bookingTOwner.setStyle("-fx-alignment: CENTER;");
        bookingTCreated.setStyle("-fx-alignment: CENTER;");
        bookingTUpdated.setStyle("-fx-alignment: CENTER;");
        bookingInstrumentFilterBox.getItems().setAll(instrumentService.getAll());
        bookingInstrumentFilterBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onRefreshBooking();
            }
        });
        bookingInstrumentFilterBox.setCellFactory(new Callback<ListView<Instrument>, ListCell<Instrument>>() {
            @Override
            public ListCell<Instrument> call(ListView<Instrument> param) {
                return new ListCell<Instrument>() {
                    @Override
                    protected void updateItem(Instrument item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.valueOf(item.getId()));
                        }
                    }
                };
            }
        });
        bookingInstrumentFilterBox.setButtonCell(new ListCell<Instrument>() {
            @Override
            protected void updateItem(Instrument item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item.getId()));
                }
            }
        });
        checkoutTId.setCellValueFactory(new PropertyValueFactory<>("id"));
        checkoutTInstId.setCellValueFactory(new PropertyValueFactory<>("instrumentId"));
        checkoutTUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        checkoutTComm.setCellValueFactory(new PropertyValueFactory<>("comment"));
        checkoutTTaken.setCellValueFactory(new PropertyValueFactory<>("takenAt"));
        checkoutTTaken.setCellFactory(createInstantCellFactory(formatter));
        checkoutTReturned.setCellValueFactory(new PropertyValueFactory<>("returnedAt"));
        checkoutTReturned.setCellFactory(createInstantCellFactory(formatter));
        checkoutTCondition.setCellValueFactory(new PropertyValueFactory<>("returnCondition"));
        checkoutTOwner.setCellValueFactory(new PropertyValueFactory<>("ownerUsername"));
        checkoutTCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        checkoutTCreated.setCellFactory(createInstantCellFactory(formatter));
        checkoutTId.setStyle("-fx-alignment: CENTER;");
        checkoutTInstId.setStyle("-fx-alignment: CENTER;");
        checkoutTUser.setStyle("-fx-alignment: CENTER;");
        checkoutTComm.setStyle("-fx-alignment: CENTER;");
        checkoutTTaken.setStyle("-fx-alignment: CENTER;");
        checkoutTReturned.setStyle("-fx-alignment: CENTER;");
        checkoutTCondition.setStyle("-fx-alignment: CENTER;");
        checkoutTOwner.setStyle("-fx-alignment: CENTER;");
        checkoutTCreated.setStyle("-fx-alignment: CENTER;");
        checkoutInstrumentFilterBox.getItems().setAll(instrumentService.getAll());
        checkoutInstrumentFilterBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onRefreshCheckout();
            }
        });
        checkoutInstrumentFilterBox.setCellFactory(new Callback<ListView<Instrument>, ListCell<Instrument>>() {
            @Override
            public ListCell<Instrument> call(ListView<Instrument> param) {
                return new ListCell<Instrument>() {
                    @Override
                    protected void updateItem(Instrument item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.valueOf(item.getId()));
                        }
                    }
                };
            }
        });
        checkoutInstrumentFilterBox.setButtonCell(new ListCell<Instrument>() {
            @Override
            protected void updateItem(Instrument item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item.getId()));
                }
            }
        });
    }

    private <T> Callback<TableColumn<T, Instant>, TableCell<T, Instant>> createInstantCellFactory(DateTimeFormatter formatter) {
        return new Callback<TableColumn<T, Instant>, TableCell<T, Instant>>() {
            @Override
            public TableCell<T, Instant> call(TableColumn<T, Instant> param) {
                return new TableCell<T, Instant>() {
                    @Override
                    protected void updateItem(Instant item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatter.format(item));
                        }
                    }
                };
            }
        };
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(250);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(250);
        alert.showAndWait();
    }

//    @FXML
//    private void onSaveAll() {
//        DirectoryChooser chooser = new DirectoryChooser();
//        chooser.setTitle("Выберите место для сохранения");
//        File parentFolder = chooser.showDialog(instrumentTable.getScene().getWindow());
//        if (parentFolder == null) {
//            return;
//        }
//        TextInputDialog dialog = new TextInputDialog("data");
//        dialog.setTitle("Имя папки");
//        dialog.setHeaderText("Введите имя папки для сохранения");
//        dialog.setContentText("Имя папки:");
//        Optional<String> result = dialog.showAndWait();
//        if (!result.isPresent() || result.get().trim().isEmpty()) {
//            return;
//        }
//        File saveFolder = new File(parentFolder, result.get().trim());
//        try {
//            saveCommand.execute(new String[]{"save", saveFolder.getAbsolutePath()});
//            showInfo("Данные сохранены в папку:\n" + saveFolder.getAbsolutePath());
//        } catch (StorageException e) {
//            showError(e.getMessage());
//        }
//    }

//    @FXML
//    private void onSaveAll() {
//        DirectoryChooser chooser = new DirectoryChooser();
//        chooser.setTitle("Выберите место для сохранения");
//        File parentFolder = chooser.showDialog(instrumentTable.getScene().getWindow());
//        if (parentFolder == null) {
//            return;
//        }
//        TextInputDialog dialog = new TextInputDialog("data");
//        dialog.setTitle("Имя папки");
//        dialog.setHeaderText("Введите имя папки для сохранения");
//        dialog.setContentText("Имя папки:");
//        Optional<String> result = dialog.showAndWait();
//        if (!result.isPresent() || result.get().trim().isEmpty()) {
//            return;
//        }
//        final File saveFolder = new File(parentFolder, result.get().trim());
//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                saveCommand.execute(new String[]{"save", saveFolder.getAbsolutePath()});
//                userService.saveUsers(saveFolder.toPath());
//                return null;
//            }
//        };
//        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                showInfo("Данные сохранены в папку:\n" + saveFolder.getAbsolutePath());
//            }
//        });
//        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                Throwable ex = task.getException();
//                showError(ex.getMessage());
//            }
//        });
//        Thread thread = new Thread(task);
//        thread.setDaemon(true);
//        thread.start();
//    }

    @FXML
    private void onSaveAll() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
//                saveCommand.execute(new String[]{"save", HelloApplication.DATA_FOLDER.toString()});
//                userService.saveUsers(HelloApplication.DATA_FOLDER);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            showInfo("Данные сохранены в папку:\n" + HelloApplication.DATA_FOLDER.toAbsolutePath());
        });
        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            showError(ex.getMessage());
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onLoadAll() {
        try {
            loadCommand.execute(new String[]{"load", HelloApplication.DATA_FOLDER.toString()});
//            userService.loadUsers(HelloApplication.DATA_FOLDER);
            instrumentTable.setItems(FXCollections.observableArrayList(instrumentService.getAll()));
            bookingTable.setItems(FXCollections.observableArrayList(bookingService.getAll()));
            checkoutTable.setItems(FXCollections.observableArrayList(checkoutService.getAll()));
            showInfo("Данные загружены из папки:\n" + HelloApplication.DATA_FOLDER.toAbsolutePath());
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

//    @FXML
//    private void onLoadAll() {
//        DirectoryChooser chooser = new DirectoryChooser();
//        chooser.setTitle("Выберите папку с данными");
//        final File folder = chooser.showDialog(instrumentTable.getScene().getWindow());
//        if (folder == null) {
//            return;
//        }
//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                loadCommand.execute(new String[]{"load", folder.getAbsolutePath()});
//                userService.loadUsers(folder.toPath());
//                return null;
//            }
//        };
//        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                instrumentTable.setItems(FXCollections.observableArrayList(instrumentService.getAll()));
//                bookingTable.setItems(FXCollections.observableArrayList(bookingService.getAll()));
//                checkoutTable.setItems(FXCollections.observableArrayList(checkoutService.getAll()));
//                showInfo("Данные загружены.");
//            }
//        });
//        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                Throwable ex = task.getException();
//                showError(ex.getMessage());
//            }
//        });
//        Thread thread = new Thread(task);
//        thread.setDaemon(true);
//        thread.start();
//    }

    //    @FXML
//    private void onLoadAll() {
//        DirectoryChooser chooser = new DirectoryChooser();
//        chooser.setTitle("Выберите папку с данными");
//        File folder = chooser.showDialog(instrumentTable.getScene().getWindow());
//        if (folder == null) {
//            return;
//        }
//        try {
//            loadCommand.execute(new String[]{"load", folder.getAbsolutePath()});
//            instrumentTable.setItems(FXCollections.observableArrayList(instrumentService.getAll()));
//            bookingTable.setItems(FXCollections.observableArrayList(bookingService.getAll()));
//            checkoutTable.setItems(FXCollections.observableArrayList(checkoutService.getAll()));
//            showInfo("Данные загружены.");
//        } catch (StorageException e) {
//            showError(e.getMessage());
//        }
//    }

    private void onLogin() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Авторизация");
        dialog.setHeaderText("Введите логин и пароль");
        Label loginLabel = new Label("Логин:");
        TextField loginField = new TextField();
        Label passwordLabel = new Label("Пароль:");
        PasswordField passwordField = new PasswordField();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(loginLabel, 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        dialog.getDialogPane().setContent(grid);
        ButtonType loginButton = new ButtonType("Войти", ButtonBar.ButtonData.OK_DONE);
        ButtonType registerButton = new ButtonType("Регистрация", ButtonBar.ButtonData.OTHER);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButton, loginButton, cancelButton);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() == cancelButton) {
            Platform.exit();
            return;
        }
        if (result.get() == registerButton) {
            onRegister();
            onLogin();
            return;
        }
        String login = loginField.getText();
        String password = passwordField.getText();
        try {
            User user = userService.login(login, password);
            CurrentUser.login(user);
            updateAccessRights();
            showInfo("Вход выполнен: " + user.getLogin());
        } catch (UserInputException e) {
            showError(e.getMessage());
            onLogin();
        }
    }

    private void updateAccessRights() {
        boolean admin = CurrentUser.isAdmin();
        deleteCheckoutButton.setVisible(admin);
        deleteCheckoutButton.setManaged(admin);
        deleteInstrumentButton.setVisible(admin);
        deleteInstrumentButton.setManaged(admin);
        updateInstrumentButton.setVisible(admin);
        updateInstrumentButton.setManaged(admin);
        showUsersButton1.setVisible(admin);
        showUsersButton1.setManaged(admin);
        showUsersButton2.setVisible(admin);
        showUsersButton2.setManaged(admin);
        showUsersButton3.setVisible(admin);
        showUsersButton3.setManaged(admin);
    }

    private boolean canEdit(String ownerUsername) {
        return CurrentUser.isAdmin() || CurrentUser.getCurrentUser().getLogin().equals(ownerUsername);
    }

    private void onRegister() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Регистрация");
        TextField loginField = new TextField();
        loginField.setPromptText("Логин");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Логин:"), 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(new Label("Пароль:"), 0, 1);
        grid.add(passwordField, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.registration(loginField.getText(), passwordField.getText(), false);
                saveCommand.execute(new String[]{"save", "data"});
                showInfo("Пользователь зарегистрирован.");
            } catch (UserInputException e) {
                showError(e.getMessage());
            } catch (StorageSaveException e) {
                showError("Пользователь создан, но не сохранён: " + e.getMessage());
            }
        }
    }

    public boolean hasCriticalLoadError() {
        return criticalLoadError;
    }

    @FXML
    private void onShowUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("users.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Пользователи");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Не удалось открыть окно пользователей.");
        }
    }
}