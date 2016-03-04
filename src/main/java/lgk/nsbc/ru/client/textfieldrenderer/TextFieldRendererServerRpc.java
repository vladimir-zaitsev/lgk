package lgk.nsbc.ru.client.textfieldrenderer;

import com.vaadin.shared.communication.ServerRpc;

public interface TextFieldRendererServerRpc extends ServerRpc {

    public void onChange(String rowKey, String columnId, String newValue);
}
