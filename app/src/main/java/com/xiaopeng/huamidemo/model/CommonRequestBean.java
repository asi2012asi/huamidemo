package com.xiaopeng.huamidemo.model;

public class CommonRequestBean {

    /**
     * msg_id : 1343506507657
     * msg_type : 2
     * service_type : 4
     * target_id : 20015
     * msg_content : {"cmd_type":2,"cmd_value":80}
     */

    private String msg_id;
    private int msg_type;
    private int service_type;
    private int target_id;
    private MsgContentBean msg_content;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public MsgContentBean getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(MsgContentBean msg_content) {
        this.msg_content = msg_content;
    }

    public static class MsgContentBean {
        /**
         * cmd_type : 2
         * cmd_value : 80
         */

        private int cmd_type;
        private int cmd_value;

        public int getCmd_type() {
            return cmd_type;
        }

        public void setCmd_type(int cmd_type) {
            this.cmd_type = cmd_type;
        }

        public int getCmd_value() {
            return cmd_value;
        }

        public void setCmd_value(int cmd_value) {
            this.cmd_value = cmd_value;
        }
    }
}
