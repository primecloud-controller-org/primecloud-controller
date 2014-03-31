package jp.primecloud.auto.ui.util;

import java.util.HashMap;
import java.util.List;

import jp.primecloud.auto.entity.crud.AuthoritySet;
import jp.primecloud.auto.entity.crud.UserAuth;
import jp.primecloud.auto.service.UserManagementService;


public class ConvertUtil {

    private String AUTH_TRUE = "1";
    private String AUTH_FALSE = "0";
    private String NAME_CUSTOM = ViewProperties.getCaption("label.custom");
    private String NAME_NOTHING = ViewProperties.getCaption("label.nothing");
    private Long NO_CUSTOM = new Long(-1);
    private Long NO_NOTHING = new Long(0);


    protected static HashMap<String, String> authsetNameMap;
    protected static HashMap<String, Long> authsetNoMap;

    public ConvertUtil() {
        authsetNameMap = new HashMap<String, String>();
        authsetNoMap = new HashMap<String, Long>();

        UserManagementService userManagementService = BeanContext.getBean(UserManagementService.class);
        List<AuthoritySet> sets = userManagementService.getAuthoritySet();

        for (AuthoritySet set : sets) {
            String setName = set.getSetName();
            Long setno     = set.getSetNo();

            StringBuilder setString = new StringBuilder();
            if (set.getFarmUse())         setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getServerMake())      setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getServerDelete())    setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getServerOperate())   setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getServiceMake())     setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getServiceDelete())   setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getServiceOperate())  setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getLbMake())          setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getLbDelete())        setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            if (set.getLbOperate())       setString.append(AUTH_TRUE);
            else                          setString.append(AUTH_FALSE);

            authsetNameMap.put(setString.toString(), setName);
            authsetNoMap.put(setString.toString(), setno);
        }
    }

    private String makeSetString(UserAuth auth) {

        //権限データがない場合は権限なしと判定
        if(auth == null) {
            return AUTH_FALSE;
        }

        StringBuilder setString = new StringBuilder();
        if (auth.getFarmUse())         setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getServerMake())      setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getServerDelete())    setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getServerOperate())   setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getServiceMake())     setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getServiceDelete())   setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getServiceOperate())  setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getLbMake())          setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getLbDelete())        setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        if (auth.getLbOperate())       setString.append(AUTH_TRUE);
        else                           setString.append(AUTH_FALSE);

        return setString.toString();
    }



    public String ConvertAuthToName(UserAuth auth) {
        String setString = makeSetString(auth);

        //頭0であれば権限無しと判定
        if (setString.startsWith(AUTH_FALSE)) {
            return NAME_NOTHING;
        }

        //キーとしての登録がなければカスタム判定
        if (!authsetNameMap.containsKey(setString)) {
            return NAME_CUSTOM;
        }

        //それ以外
        return authsetNameMap.get(setString);
    }

    public Long ConvertAuthToSetNo(UserAuth auth) {
        String setString = makeSetString(auth);

        //頭0であれば権限無しと判定
        if (setString.startsWith(AUTH_FALSE)) {
            return NO_NOTHING;
        }

        //キーとしての登録がなければカスタム判定
        if (!authsetNameMap.containsKey(setString)) {
            return NO_CUSTOM;
        }

        //それ以外
        return authsetNoMap.get(setString);
    }
}
