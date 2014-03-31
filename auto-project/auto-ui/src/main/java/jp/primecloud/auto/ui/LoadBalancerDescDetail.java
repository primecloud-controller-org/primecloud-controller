package jp.primecloud.auto.ui;

import jp.primecloud.auto.service.dto.LoadBalancerDto;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <p>
 * 現在は未使用です。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class LoadBalancerDescDetail extends Panel {

    DetailInfo left = new DetailInfo();

    DetailParameters right = new DetailParameters();

    public LoadBalancerDescDetail() {
        addStyleName(Reindeer.PANEL_LIGHT);
        setHeight("100%");

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setWidth("100%");
        layout.setHeight("100%");
        layout.addStyleName("loadbalancer-desc-detail");
        layout.setMargin(true);
        layout.setSpacing(true);

        HorizontalLayout hlPanels = new HorizontalLayout();
        hlPanels.setWidth("100%");
        hlPanels.setHeight("100%");
        hlPanels.setMargin(true);
        hlPanels.setSpacing(true);
        hlPanels.addStyleName("loadbalancer-desc-detail");

        left.setWidth("250px");
        right.setWidth("100%");
        right.setHeight("100%");

        hlPanels.addComponent(left);
        hlPanels.addComponent(right);
        hlPanels.setExpandRatio(right, 100);

        layout.addComponent(hlPanels);
        layout.setExpandRatio(hlPanels, 1.0f);

    }


    class DetailInfo extends VerticalLayout {

        Label loadbalancerName = new Label();

        DetailInfo() {
            addStyleName("loadbalancer-desc-detail-info");
            setCaption(ViewProperties.getCaption("label.loadBalancerDetailInfo"));
            setSizeFull();

            setMargin(false, false, false, true);
            setSpacing(true);
            addStyleName("loadbalancer-desc-detail-info");

            //名前ラベル追加
            addComponent(loadbalancerName);

        }

        public void setItem(LoadBalancerDto dto) {
            if (dto != null) {
                // TODO: ロードバランサー名の表示
//                loadbalancerName.setCaption(null);

                //ボタンの表示
//                operation.refresh(dto);

            } else {
                loadbalancerName.setCaption(null);
                //ボタンの無効化・非表示
//                operation.hide();
            }
        }
    }

    class DetailParameters extends Table {

        DetailParameters() {

            addStyleName("loadbalancer-desc-detail-param");
            setCaption(ViewProperties.getCaption("label.loadBalancerDetailParams"));

            //テーブル基本設定
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT);
            setSortDisabled(true);
            setMultiSelect(false);
            setImmediate(false);

            //カラム設定
            addContainerProperty("Kind", String.class, null);
            addContainerProperty("Name", String.class, null);
            addContainerProperty("Value", String.class, null);

            setColumnExpandRatio("Value", 100);

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

        }

        public void setHeaders(){
            setColumnHeaders(new String[]{
                    ViewProperties.getCaption("field.categoryName"),
                    ViewProperties.getCaption("field.parameterName"),
                    ViewProperties.getCaption("field.parameterValue")}
            );
        }
    }

    public void initializeData() {
        left.setItem(null);
        right.getContainerDataSource().removeAllItems();
    }

}
