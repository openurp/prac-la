[#ftl]
[@b.head/]
[@b.toolbar title="新增/修改批次"]bar.addBack();[/@]
[@b.tabs]
  [@b.form theme="list" action=b.rest.save(laSession)]
    [@b.select label="学年学期" name="laSession.semester.id" items={} required="true" ]
      <option value="">...</option>
      [#list semesters?sort_by("code")?reverse as semester]
        <option value="${semester.id}" [#if semester.id = currentSemester.id]selected[/#if]>${(semester.schoolYear)!}学年${(semester.name?replace('0','第'))!}学期</option>
      [/#list]
    [/@]
    [@b.textfield name="laSession.name" label="批次名称" value="${laSession.name!}" required="true" maxlength="80" style="width:200px;"/]
    [@b.startend label="开放时间"
      name="laSession.beginAt,laSession.endAt" required="true,true"
      start=(laSession.beginAt)! end=(laSession.endAt)! format="yyyy-MM-dd HH:mm" style="width:200px"/]
    [@b.textfield name="laSession.grades" label="可选年级" value="${laSession.grades!}" maxlength="200" style="width:200px;"  required="true" /]
    [@b.textfield name="laSession.minGpa" label="最低平均绩点" value="${laSession.minGpa!}" maxlength="20" style="width:40px;"  required="true" /]
    [@b.formfoot]
      [@b.reset/]&nbsp;&nbsp;[@b.submit value="action.submit"/]
    [/@]
  [/@]
[/@]
[@b.foot/]
