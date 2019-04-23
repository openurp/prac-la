[#ftl]
[@b.head/]
[@b.toolbar title="可报名单位"]bar.addBack();[/@]
[#if sessions ?? && sessions?size>0]
  [#if options?? && options?size>0]
    [@b.grid items=options var="option" sortable="false"]
      [@b.row]
        [@b.col width="5%" title="序号"]${option_index+1 }[/@]
		    [@b.col width="15%" property="corporation.name" title="单位名称"/]
		    [@b.col width="30%" property="benefits" title="要求"/]
		    [@b.col width="30%" property="request" title="福利条件"/]
		    [@b.col width="5%" property="capacity" title="人数上限"/]
		    [@b.col width="5%" property="actual" title="实际人数"/]
        [@b.col width="10%" title="操作"][#if !chooseOptions?seq_contains(option)]<button onclick = "choose(${option.id})">报名</button>[#else]已报名[/#if][/@]
      [/@]
    [/@]
  [#else]目前没有可报名单位
  [/#if]
[#else]法律助理报名还未开始
[/#if]
[@b.form name="optionSearchForm" action="!choose"/]

<script>
function choose(optionId)
{
   var adjust=prompt("是否接受调剂（请填写‘是’或‘否’）","是");
   if(adjust)
   {
     var form =  document.optionSearchForm;
     bg.form.submit(form,"${b.url('!choose')}"+"?option.id=" +optionId +"&session.id=" +${session.id } +"&adjust=" +adjust);
   }
}
</script>
[@b.foot/]
