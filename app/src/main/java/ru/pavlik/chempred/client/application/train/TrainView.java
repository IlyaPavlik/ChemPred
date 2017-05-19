package ru.pavlik.chempred.client.application.train;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import ru.pavlik.chempred.client.application.base.BasePopupView;
import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.client.model.dao.DescriptorType;

import javax.inject.Inject;
import java.util.*;

public class TrainView extends BasePopupView<TrainUiHandler> implements TrainPresenter.MyView {

    static final String COLUMN_CHECK = "check";
    static final String COLUMN_NAME = "Наименование";
    static final String COLUMN_LOW_FACTOR = "НКПВ";

    @UiField
    ListBox listBox;
    @UiField
    Heading placeholder;
    @UiField
    TextBox search;
    @UiField
    Button remove;
    @UiField
    Panel addAll;
    @UiField
    Button train;

    @UiField
    NavTabs navTabs;
    @UiField
    TabContent tabContent;

    private Map<DescriptorType, ListDataProvider> listProviders;
    private List<CompoundDao> sourceCompounds;
    private List<CompoundDao> selectedCompounds;

    private HandlerRegistration itemClickHandler;
    private HandlerRegistration addAllHandler;

    interface Binder extends UiBinder<PopupPanel, TrainView> {
    }

    @Inject
    public TrainView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
        listProviders = new HashMap<>();
        selectedCompounds = new ArrayList<>();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        search.addKeyUpHandler(event -> getUiHandlers().searchCompound(search.getValue()));

        if (addAllHandler != null) {
            addAllHandler.removeHandler();
        }
        addAllHandler = addAll.addDomHandler(event -> {
            for (CompoundDao compound : sourceCompounds) {
                getUiHandlers().loadCompoundDescriptors(compound);
            }
        }, ClickEvent.getType());

        getUiHandlers().loadCompounds();
        getUiHandlers().loadSourceDescriptors();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        tabContent.clear();
        navTabs.clear();
    }

    @Override
    public void showCompounds(List<CompoundDao> compounds) {
        listBox.clear();
        this.sourceCompounds = compounds;
        for (CompoundDao compound : compounds) {
            listBox.addItem(compound.getName());
        }

        if (itemClickHandler != null) {
            itemClickHandler.removeHandler();
        }
        itemClickHandler = listBox.addClickHandler(event -> {
            Set<Integer> indexes = getSelectedIndexes();
            for (Integer index : indexes) {
                getUiHandlers().loadCompoundDescriptors(compounds.get(index));
            }
            listBox.setSelectedIndex(-1);
        });
    }

    @Override
    public void showTables(DescriptorType type, List<String> descriptors) {
        TabPane tabPane = new TabPane();
        tabPane.setHeight("100%");
        tabPane.setWidth("100%");

        TabListItem tabListItem = new TabListItem(type.getTitle());
        tabListItem.setDataTargetWidget(tabPane);


        CellTable<Map<String, String>> dataGrid = new CellTable<>(200);//TODO replace hardcode
        dataGrid.setBordered(true);
        dataGrid.setCondensed(true);
        dataGrid.setStriped(true);
        dataGrid.setHover(true);
        dataGrid.getElement().getStyle().setTableLayout(Style.TableLayout.FIXED);

        ListDataProvider<Map<String, String>> listDataProvider = new ListDataProvider<>();
        Header<Boolean> header = new Header<Boolean>(new CheckboxCell()) {
            @Override
            public Boolean getValue() {
                return false;
            }
        };
        header.setUpdater(value -> {
            for (Map<String, String> item : dataGrid.getVisibleItems()) {
                item.put(COLUMN_CHECK, String.valueOf(value));
            }
            listDataProvider.refresh();
        });
        Column<Map<String, String>, Boolean> checkColumn = new Column<Map<String, String>, Boolean>(new CheckboxCell()) {
            @Override
            public Boolean getValue(Map<String, String> object) {
                return Boolean.parseBoolean(object.get(COLUMN_CHECK));
            }
        };
        checkColumn.setFieldUpdater((index, object, value) -> object.put(COLUMN_CHECK, String.valueOf(value)));
        dataGrid.setColumnWidth(checkColumn, "30px");
        dataGrid.addColumn(checkColumn, header);

        TextColumn<Map<String, String>> nameColumn = new TextColumn<Map<String, String>>() {
            @Override
            public String getValue(Map<String, String> values) {
                return values.get(COLUMN_NAME);
            }
        };
        dataGrid.setColumnWidth(nameColumn, "150px");
        dataGrid.addColumn(nameColumn, COLUMN_NAME);

        TextColumn<Map<String, String>> lowFactorColumn = new TextColumn<Map<String, String>>() {
            @Override
            public String getValue(Map<String, String> values) {
                return values.get(COLUMN_LOW_FACTOR);
            }
        };
        dataGrid.setColumnWidth(lowFactorColumn, "80px");
        dataGrid.addColumn(lowFactorColumn, COLUMN_LOW_FACTOR);

        for (String descriptor : descriptors) {
            TextColumn<Map<String, String>> column = new TextColumn<Map<String, String>>() {
                @Override
                public String getValue(Map<String, String> descriptors) {
                    if (descriptors.containsKey(descriptor)) {
                        return String.valueOf(descriptors.get(descriptor));
                    } else {
                        return String.valueOf(0);
                    }
                }
            };
            dataGrid.setColumnWidth(column, "80px");
            dataGrid.addColumn(column, descriptor);
        }
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidth("0px");
        scrollPanel.setHeight("0px");
        scrollPanel.add(dataGrid);
        tabPane.add(scrollPanel);
        tabListItem.addClickHandler(event -> Scheduler.get().scheduleDeferred(() -> {
            scrollPanel.setWidth(tabPane.getElement().getClientWidth() + "px");
            scrollPanel.setHeight((tabPane.getElement().getClientHeight() - 40) + "px");
        }));

        //select first tab
        if (navTabs.getWidgetCount() == 0) {
            tabListItem.setActive(true);
            tabPane.setActive(true);
            Scheduler.get().scheduleDeferred(() -> {
                scrollPanel.setWidth(tabPane.getElement().getClientWidth() + "px");
                scrollPanel.setHeight((tabPane.getElement().getClientHeight() - 40) + "px");
            });
        }
        tabContent.add(tabPane);
        navTabs.add(tabListItem);

        listDataProvider.addDataDisplay(dataGrid);
        listProviders.put(type, listDataProvider);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addCompound(CompoundDao compound, Map<String, String> descriptors) {
        Map<String, String> columnValues = new HashMap<>();
        columnValues.put(COLUMN_NAME, compound.getName());
        columnValues.put(COLUMN_CHECK, "false");
        columnValues.put(COLUMN_LOW_FACTOR, NumberFormat.getFormat("#.##").format(compound.getLowFactor()));
        columnValues.putAll(descriptors);

        for (ListDataProvider<Map<String, String>> listDataProvider : listProviders.values()) {
            listDataProvider.getList().add(columnValues);
            listDataProvider.flush();
        }
        selectedCompounds.add(compound);
    }

    @Override
    public void showTrainProgress() {
        train.state().loading();
    }

    @Override
    public void hideTrainProgress() {
        train.state().reset();
    }

    @UiHandler("remove")
    public void onRemoveCompoundClick(ClickEvent event) {
        for (ListDataProvider dataProvider : listProviders.values()) {
            List<Map<String, String>> columnsList = dataProvider.getList();
            for (int i = 0; i < columnsList.size(); i++) {
                Map<String, String> columns = columnsList.get(i);
                if (Boolean.parseBoolean(columns.get(COLUMN_CHECK))) {
                    columnsList.remove(columns);
                    removeSelectCompound(columns.get(COLUMN_NAME));
                    i--;
                }
            }
            dataProvider.flush();
        }
    }

    @UiHandler("train")
    public void onTrainClick(ClickEvent event) {
        getUiHandlers().handlerTrain(selectedCompounds);
    }

    private void visiblePlaceholder(boolean visible) {
        placeholder.setVisible(visible);
    }

    private Set<Integer> getSelectedIndexes() {
        Set<Integer> indexes = new HashSet<>();
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (listBox.isItemSelected(i)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private void removeSelectCompound(final String compoundName) {
        for (int i = 0; i < selectedCompounds.size(); i++) {
            CompoundDao compound = selectedCompounds.get(i);
            if (compound.getName().equalsIgnoreCase(compoundName)) {
                selectedCompounds.remove(i);
                i--;
            }
        }
    }
}