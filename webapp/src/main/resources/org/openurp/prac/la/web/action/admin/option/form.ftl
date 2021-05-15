[#ftl]
[@b.head/]
[@b.toolbar title="新增/修改项目单位要求"]bar.addBack();[/@]
  [@b.form theme="list" action=b.rest.save(laOption)]
    [@b.select label="批次" name="laOption.session.id" items=sessions?sort_by("beginAt")?reverse required="true" value=laOption.session style="width:200px"/]
    [@b.select label="单位" name="laOption.corporation.id" items=corporations required="true"  style="width:200px;" value=laOption.corporation! /]
    [@b.textarea name="laOption.requirement" label="要求" value="${laOption.requirement!}" maxlength="2000"  cols="100" rows="10"/]
    [@b.textarea name="laOption.remark" label="备注" value="${laOption.remark!}" maxlength="2000"  cols="100" rows="10"/]
    [@b.textfield name="laOption.capacity" label="最大面试人数" value="${laOption.capacity}" required="true" style="width:50px;"/]
    [@b.textfield name="laOption.enrollLimit" label="拟录取人数" value="${laOption.enrollLimit}" required="true" style="width:50px;"/]
    [@b.formfoot]
      <input type="hidden" name="laOption.semester.id" value="${semester.id}"/>
      [@b.reset/]&nbsp;&nbsp;[@b.submit value="action.submit"/]
    [/@]
  [/@]
[@b.foot/]
