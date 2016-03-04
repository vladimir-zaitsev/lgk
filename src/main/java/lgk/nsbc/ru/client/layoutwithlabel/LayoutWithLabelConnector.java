package lgk.nsbc.ru.client.layoutwithlabel;

import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.shared.ui.Connect;
import lgk.nsbc.ru.LayoutWithLabelRenderer;

/**
 * Created by Роман on 04.03.2016.
 */
@Connect(LayoutWithLabelRenderer.class)
public class LayoutWithLabelConnector extends AbstractRendererConnector<String> {
	LayoutWithLabelServerRpc rpc = RpcProxy.create(LayoutWithLabelServerRpc.class,this);


}
