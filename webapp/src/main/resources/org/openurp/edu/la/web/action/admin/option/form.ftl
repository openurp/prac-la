[#ftl]
[@b.head/]
[@b.toolbar title="新增/修改项目单位要求"]bar.addBack();[/@]
  <script type="text/javascript" crossorigin="anonymous" src="${base}/static/js/ajax-chosen.js"></script>
[@b.tabs]
  [@b.form theme="list" action=b.rest.save(laOption)]
    [@b.select label="学年学期" name="laOption.semester.id" items={} required="true"]
      <option value="">...</option>
      [#list semesters?sort_by("code")?reverse as semester]
        <option value="${semester.id}" [#if semester.id = currentSemester.id]selected[/#if]>${(semester.schoolYear)!}学年${(semester.name?replace('0','第'))!}学期</option>
      [/#list]
    [/@]
    [@b.field label="单位"]
      <select id="corporationId" name="laOption.corporation.id" style="width:200px;">
        <option value='${(laOption.corporation.id)!}' selected>${(laOption.corporation.name)!}</option>
      </select>
    [/@]
    [@b.textarea name="laOption.request" label="要求" value="${laOption.request!}" maxlength="2000"  cols="150" rows="20"/]
    [@b.textarea name="laOption.benefits" label="福利条件" value="${laOption.benefits!}" maxlength="2000"  cols="150" rows="20"/]
    [@b.textfield name="laOption.capacity" label="人数上限" value="${laOption.capacity}" required="true" maxlength="80" style="width:200px;"/]
    [@b.formfoot]
      [@b.reset/]&nbsp;&nbsp;[@b.submit value="action.submit"/]
    [/@]
  [/@]
[/@]
<script>
  jQuery("#corporationId").ajaxChosen(
          {
              method: 'GET',
              url:  "${b.url('!corporation?pageNo=1&pageSize=10')}"
          }
          , function (data) {
              var items = {};
              var dataObj = eval("(" + data + ")");
              jQuery.each(dataObj.corporations, function (i, corporation) {
                  items[corporation.id] = corporation.name;
              });
              return items;
          },
          {width:"400px"}
        );
</script>
[@b.foot/]
