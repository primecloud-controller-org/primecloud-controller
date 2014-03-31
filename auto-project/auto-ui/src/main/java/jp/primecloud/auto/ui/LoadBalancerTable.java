package jp.primecloud.auto.ui;

import java.util.Collection;

import jp.primecloud.auto.common.status.LoadBalancerStatus;
import jp.primecloud.auto.service.dto.ComponentDto;
import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.service.dto.PlatformDto;
import jp.primecloud.auto.ui.data.LoadBalancerDtoContainer;
import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.VaadinUtils;
import jp.primecloud.auto.ui.util.ViewProperties;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * <p>
 * ロードバランサ画面の画面中央のロードバランサ一覧を生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerTable  extends Table {

    final String COLUMN_HEIGHT = "28px";

    final MyCloudTabs sender;

    //項目名
    String[] CAPNAME = {
            ViewProperties.getCaption("field.no"),
            ViewProperties.getCaption("field.loadBalancerName"),
            ViewProperties.getCaption("field.loadBalancerStatus"),
            ViewProperties.getCaption("field.loadBalancerHostname"),
            ViewProperties.getCaption("field.loadBalancerType"),
            ViewProperties.getCaption("field.loadBalancerService")
            };

    LoadBalancerTable(String caption, Container container, final MyCloudTabs sender) {
        super(caption, container);

        this.sender = sender;
        setVisibleColumns(new Object[] {});

        setWidth("100%");
        if ( this.isEnabled() ) {
            setHeight("100%");
        }

        setPageLength(0);
        setSortDisabled(true);
        setColumnReorderingAllowed(false);
        setColumnCollapsingAllowed(false);
        setSelectable(true);
        setMultiSelect(false);
        setImmediate(true);
        setStyleName("loadbalancer-table");
        setNullSelectionAllowed(false);
        setCacheRate(0.1);


        addGeneratedColumn("no", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto p = (LoadBalancerDto) itemId;
                Label nlbl = new Label(String.valueOf(p.getLoadBalancer().getLoadBalancerNo()));
                return nlbl;
            }
        });

        addGeneratedColumn("name", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto p = (LoadBalancerDto) itemId;
                Label nlbl;
                if (StringUtils.isEmpty(p.getLoadBalancer().getComment())) {
                    nlbl = new Label(p.getLoadBalancer().getLoadBalancerName(), Label.CONTENT_TEXT);
                } else {
                    String name = p.getLoadBalancer().getComment() + "\n[" + p.getLoadBalancer().getLoadBalancerName() + "]";
                    nlbl = new Label(name, Label.CONTENT_PREFORMATTED);
                    nlbl.setHeight(COLUMN_HEIGHT);
                }
                return nlbl;
            }
        });

        addGeneratedColumn("status", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto p = (LoadBalancerDto) itemId;
                LoadBalancerStatus status = LoadBalancerStatus.fromStatus(p.getLoadBalancer().getStatus());
                String statusString = status.name().substring(0, 1).toUpperCase() + status.name().substring(1).toLowerCase();
                Icons icon;
                //ロードバランサーがRUNNIGだが、リスナ一覧にリスナーが存在しない(+WARNING)
                if (status == LoadBalancerStatus.RUNNING && p.getLoadBalancerListeners().size() == 0) {
                        icon = Icons.RUN_WARNING;
                } else {
                    icon = Icons.fromName(statusString);
                }

                Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(LoadBalancerTable.this, icon)
                                                        + "\"><div>"+ statusString + "</div>",Label.CONTENT_XHTML);
                slbl.setHeight(COLUMN_HEIGHT);
                return slbl;
            }
        });

        addGeneratedColumn("hostName", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto p = (LoadBalancerDto) itemId;

                String hostname = p.getLoadBalancer().getCanonicalName();
                //if (p.getComponentLoadBalancerDto() != null) {
                //    hostname = p.getComponentLoadBalancerDto().getIpAddress();
                //}

                Label nlbl = new Label("",Label.CONTENT_TEXT);
                if (hostname != null ){
                    nlbl.setValue(hostname);
                }
                return nlbl;
            }
        });

        addGeneratedColumn("type", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto p = (LoadBalancerDto) itemId;
                PlatformDto platformDto = p.getPlatform();

                // TODO: アイコン名の取得ロジックのリファクタリング
                Icons icon = Icons.NONE;
                if ("aws".equals(platformDto.getPlatform().getPlatformType())) {
                    if (platformDto.getPlatformAws().getEuca()) {
                        icon = Icons.EUCALYPTUS;
                    } else {
                        icon = Icons.AWS;
                    }
                } else if ("vmware".equals(platformDto.getPlatform().getPlatformType())) {
                    icon = Icons.VMWARE;
                } else if ("nifty".equals(platformDto.getPlatform().getPlatformType())) {
                    icon = Icons.NIFTY;
                } else if ("cloudstack".equals(platformDto.getPlatform().getPlatformType())) {
                    icon = Icons.CLOUD_STACK;
                }

                String type = ViewProperties.getLoadBalancerType(p.getLoadBalancer().getType());
                Label slbl = new Label("<img src=\"" + VaadinUtils.getIconPath(LoadBalancerTable.this, icon) + "\"><div>"
                        + type + "</div>" , Label.CONTENT_XHTML);

//                slbl.setHeight(COLUMN_HEIGHT);
                return slbl;
            }
        });

        addGeneratedColumn("serviceName", new ColumnGenerator() {
            public Component generateCell(Table source, Object itemId, Object columnId) {
                LoadBalancerDto p = (LoadBalancerDto) itemId;
                ComponentDto componentDto=null;
                for (ComponentDto dto : (Collection<ComponentDto> )sender.serviceTable.getItemIds()){
                    if (dto.getComponent().getComponentNo().equals(p.getLoadBalancer().getComponentNo())){
                        componentDto=dto;
                        break;
                    }
                }

                Label nlbl;
                if (StringUtils.isEmpty(componentDto.getComponent().getComment())) {
                    nlbl = new Label(componentDto.getComponent().getComponentName(), Label.CONTENT_TEXT);
                } else {
                    String name = componentDto.getComponent().getComment() + "\n[" + componentDto.getComponent().getComponentName() + "]";
                    nlbl = new Label(name, Label.CONTENT_PREFORMATTED);
                    nlbl.setHeight(COLUMN_HEIGHT);
                }
                return nlbl;
            }
        });

        //テーブルに項目名を設定
        setColumnHeaders(CAPNAME);

        //テーブルのカラムに対してStyleNameを設定
        setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Object itemId, Object propertyId) {

                if (propertyId == null) {
                    return "";
                } else {
                    return propertyId.toString().toLowerCase();
                }
            }
        });

        setColumnExpandRatio("hostName", 100);
        addListener(Table.ValueChangeEvent.class, sender, "tableRowSelected");

    }

    public void refreshData() {
        ((LoadBalancerDtoContainer) getContainerDataSource()).refresh2(this);
    }

    public void refreshDesc() {
        sender.refreshDesc(this);
    }
}
