package andhook.test;

import java.util.List;

public class NativeRespose {
    List<String> mParam;
    List<String> mRequest;

    NativeRespose() {
    }

    NativeRespose(List<String> reqCode, List<String> param) {
        mParam = param;
        mRequest = reqCode;
    }
    
    private String getParam(String key) {
        String param = "";
        int index;

        for(int i = 0; i < mParam.size(); i++) {
            param = mParam.get(i);
            if(param.startsWith(key)) {
                index = param.indexOf("=");
                return param.substring(index + 1, param.length() - index);
            }
        }
        return param;
    }

    public String getResult() {
        String res = "";
        String param;
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < mRequest.size(); i++) {
            if(mRequest.get(i).equals("Nssig3")) {
                param = getParam("sig3");
                if(!param.isEmpty()) {
                    res = getNssig3(param);
                    sb.append("Nssig3=" + res);
                }
            }
            
            if(mRequest.get(i).equals("egid")) {
                param = getParam("devInfo");
                if(!param.isEmpty()) {
                    res = egid(param);
                    sb.append("egid=" + res);
                }
            }
   
            if(mRequest.get(i).equals("phoneCode")) {
                param = getParam("phoneAuthCode");
                if(!param.isEmpty()) {
                    res = phoneCode(param);
                    sb.append("phoneCode=" + res);
                }
            }

        }
        return String.valueOf(sb);
    }

    private String getNssig3(String param) {
        String res = "";

        return res;
    }

    private String egid(String devInfo) {
        String res = "";

        return res;
    }

    private String phoneCode(String phoneAuthCode) {
        String res = "";

        return res;
    }

}